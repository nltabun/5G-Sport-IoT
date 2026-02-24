package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.example.database.entity.HeartRate;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;
import org.example.database.entity.RrData;
import org.example.database.repository.HeartRateRepository;
import org.example.database.repository.RrDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeartRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartRateService.class);
    private static final int RR_BATCH_SIZE = 500;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PicoService picoService;
    private final MovesenseService movesenseService;
    private final HeartRateRepository heartRateRepository;
    private final RrDataRepository rrDataRepository;

    private final Object rrBufferLock = new Object();
    private final List<RrData> rrBuffer = new ArrayList<>();

    public HeartRateService(PicoService picoService,
                            MovesenseService movesenseService,
                            HeartRateRepository heartRateRepository,
                            RrDataRepository rrDataRepository) {
        this.picoService = picoService;
        this.movesenseService = movesenseService;
        this.heartRateRepository = heartRateRepository;
        this.rrDataRepository = rrDataRepository;
    }

    public void handleJson(String message) throws JsonProcessingException {
        HeartRate heartRate = objectMapper.readValue(message, HeartRate.class);
        Pico pico = heartRate.getPico();
        Movesense movesense = heartRate.getMovesense();

        if (pico != null && !picoService.existsInDatabase(pico)) {
            picoService.save(pico);
        }

        if (movesense != null && !movesenseService.existsInDatabase(movesense)) {
            movesenseService.save(movesense);
        }

        saveHeartRateData(heartRate);
        enqueueRrData(heartRate.getRrData());
    }

    public List<HeartRate> findAllHeartRates() {
        List<HeartRate> heartRates = (List<HeartRate>) heartRateRepository.findAll();
        for (HeartRate heartRate : heartRates) {
            List<RrData> rrData = rrDataRepository.findByHeartRateId(heartRate.getId());
            heartRate.setRrData(rrData);
        }
        return heartRates;
    }

    public HeartRate findHeartRateById(long id) {
        HeartRate heartRate = heartRateRepository.findById(id);
        if (heartRate != null) {
            List<RrData> rrData = rrDataRepository.findByHeartRateId(id);
            heartRate.setRrData(rrData);
        }
        return heartRate;
    }

    public List<HeartRate> findHeartRateByTimestampUtcBetween(Long start, Long end) {
        List<HeartRate> heartRateList = heartRateRepository.findByTimestampUtcBetween(start, end);
        for (HeartRate heartRate : heartRateList) {
            List<RrData> rrData = rrDataRepository.findByHeartRateId(heartRate.getId());
            heartRate.setRrData(rrData);
        }
        return heartRateList;
    }

    private void saveHeartRateData(HeartRate heartRate) {
        heartRateRepository.save(heartRate);
        LOGGER.info("Heart rate header saved");
    }

    private void enqueueRrData(List<RrData> rrDataArray) {
        if (rrDataArray == null || rrDataArray.isEmpty()) return;

        List<RrData> toFlush = null;

        synchronized (rrBufferLock) {
            rrBuffer.addAll(rrDataArray);

            if (rrBuffer.size() >= RR_BATCH_SIZE) {
                toFlush = new ArrayList<>(rrBuffer);
                rrBuffer.clear();
            }
        }

        if (toFlush != null) {
            flushRrData(toFlush);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void flushRrBufferOnTimer() {
        List<RrData> toFlush = null;

        synchronized (rrBufferLock) {
            if (!rrBuffer.isEmpty()) {
                toFlush = new ArrayList<>(rrBuffer);
                rrBuffer.clear();
            }
        }

        if (toFlush != null) {
            flushRrData(toFlush);
        }
    }

    @Transactional
    protected void flushRrData(List<RrData> batch) {
        rrDataRepository.saveAll(batch);
        rrDataRepository.flush();
        LOGGER.info("Flushed RR batch: {} rows", batch.size());
    }

    @PreDestroy
    public void shutdownFlush() {
        flushRrBufferOnTimer();
    }
}