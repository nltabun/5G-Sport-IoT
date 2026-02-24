package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.example.database.entity.Ecg;
import org.example.database.entity.EcgSample;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;
import org.example.database.repository.EcgRepository;
import org.example.database.repository.EcgSampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Service
public class EcgService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EcgService.class);
    private static final int SAMPLE_BATCH_SIZE = 500; // tune as needed

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PicoService picoService;
    private final MovesenseService movesenseService;
    private final EcgRepository ecgRepository;
    private final EcgSampleRepository ecgSampleRepository;

    private final Object sampleBufferLock = new Object();
    private final List<EcgSample> sampleBuffer = new ArrayList<>(SAMPLE_BATCH_SIZE);

    public EcgService(PicoService picoService,
                      MovesenseService movesenseService,
                      EcgRepository ecgRepository,
                      EcgSampleRepository ecgSampleRepository) {
        this.picoService = picoService;
        this.movesenseService = movesenseService;
        this.ecgRepository = ecgRepository;
        this.ecgSampleRepository = ecgSampleRepository;
    }

    public void handleJson(String message) throws JsonProcessingException {
        Ecg ecg = objectMapper.readValue(message, Ecg.class);
        Pico pico = ecg.getPico();
        Movesense movesense = ecg.getMovesense();

        if (pico != null && !picoService.existsInDatabase(pico)) {
            picoService.save(pico);
        }

        if (movesense != null && !movesenseService.existsInDatabase(movesense)) {
            movesenseService.save(movesense);
        }

        saveEcgData(ecg);
        enqueueSamples(ecg.getEcgSamples());
    }

    public List<Ecg> findAllEcg() {
        List<Ecg> ecgList = ecgRepository.findAll();
        for (Ecg ecg : ecgList) {
            List<EcgSample> samples = ecgSampleRepository.findByEcgId(ecg.getId());
            ecg.setEcgSamples(samples);
        }
        return ecgList;
    }

    public Ecg findEcgById(long id) {
        Ecg ecg = ecgRepository.findById(id);
        if (ecg != null) {
            List<EcgSample> samples = ecgSampleRepository.findByEcgId(id);
            ecg.setEcgSamples(samples);
        }
        return ecg;
    }

    public List<Ecg> findEcgByTimestampUtcBetween(Long start, Long end) {
        List<Ecg> ecgList = ecgRepository.findByTimestampUtcBetween(start, end);
        for (Ecg ecg : ecgList) {
            List<EcgSample> samples = ecgSampleRepository.findByEcgId(ecg.getId());
            ecg.setEcgSamples(samples);
        }
        return ecgList;
    }

    public void saveEcgData(Ecg ecg) {
        ecgRepository.save(ecg);
        LOGGER.info("ECG data saved to database");
    }

    private void enqueueSamples(List<EcgSample> samples) {
        if (samples == null || samples.isEmpty()) return;

        List<EcgSample> toFlush = null;

        synchronized (sampleBufferLock) {
            sampleBuffer.addAll(samples);

            if (sampleBuffer.size() >= SAMPLE_BATCH_SIZE) {
                toFlush = new ArrayList<>(sampleBuffer);
                sampleBuffer.clear();
            }
        }

        if (toFlush != null) {
            flushSamples(toFlush);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void flushSampleBufferOnTimer() {
        List<EcgSample> toFlush = null;

        synchronized (sampleBufferLock) {
            if (!sampleBuffer.isEmpty()) {
                toFlush = new ArrayList<>(sampleBuffer);
                sampleBuffer.clear();
            }
        }

        if (toFlush != null) {
            flushSamples(toFlush);
        }
    }

    @Transactional
    protected void flushSamples(List<EcgSample> batch) {
        ecgSampleRepository.saveAll(batch);
        ecgSampleRepository.flush();
        LOGGER.info("Flushed ECG sample batch: {} samples", batch.size());
    }

    @PreDestroy
    public void shutdownFlush() {
        flushSampleBufferOnTimer();
    }
}