package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.example.database.entity.Gnss;
import org.example.database.entity.Pico;
import org.example.database.repository.GnssRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GnssService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GnssService.class);
    private static final int BATCH_SIZE = 500; // tune as needed

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PicoService picoService;
    private final GnssRepository gnssRepository;

    private final Object bufferLock = new Object();
    private final List<Gnss> buffer = new ArrayList<>(BATCH_SIZE);

    public GnssService(PicoService picoService, GnssRepository gnssRepository) {
        this.picoService = picoService;
        this.gnssRepository = gnssRepository;
    }

    public void handleJson(String message) throws JsonProcessingException {
        Gnss gnss = objectMapper.readValue(message, Gnss.class);
        Pico pico = gnss.getPico();

        if (pico != null && !picoService.existsInDatabase(pico)) {
            picoService.save(pico);
        }

        enqueueGnss(gnss);
    }

    public List<Gnss> findAllGnss() {
        return gnssRepository.findAll();
    }

    public Gnss findGnssById(long id) {
        return gnssRepository.findById(id);
    }

    public List<Gnss> findGnssByTimestampUtcBetween(long start, long end) {
        return gnssRepository.findByTimestampUtcBetween(start, end);
    }

    // Batching
    private void enqueueGnss(Gnss gnss) {
        List<Gnss> toFlush = null;

        synchronized (bufferLock) {
            buffer.add(gnss);
            if (buffer.size() >= BATCH_SIZE) {
                toFlush = new ArrayList<>(buffer);
                buffer.clear();
            }
        }

        if (toFlush != null) {
            flushBatch(toFlush);
        }
    }
    
    @Scheduled(fixedDelay = 1000)
    public void flushOnTimer() {
        List<Gnss> toFlush = null;

        synchronized (bufferLock) {
            if (!buffer.isEmpty()) {
                toFlush = new ArrayList<>(buffer);
                buffer.clear();
            }
        }

        if (toFlush != null) {
            flushBatch(toFlush);
        }
    }

    @Transactional
    protected void flushBatch(List<Gnss> batch) {
        gnssRepository.saveAll(batch);
        gnssRepository.flush();
        LOGGER.info("Flushed GNSS batch: {} rows", batch.size());
    }

    @PreDestroy
    public void shutdownFlush() {
        flushOnTimer();
    }
}