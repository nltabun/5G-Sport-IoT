package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.example.database.entity.Imu;
import org.example.database.entity.ImuCoordinate;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;
import org.example.database.repository.ImuCoordinateRepository;
import org.example.database.repository.ImuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImuService.class);
    private static final int COORDINATE_BATCH_SIZE = 500;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PicoService picoService;
    private final MovesenseService movesenseService;
    private final ImuRepository imuRepository;
    private final ImuCoordinateRepository imuCoordinateRepository;

    private final Object coordinateBufferLock = new Object();
    private final List<ImuCoordinate> coordinateBuffer = new ArrayList<>();

    public ImuService(PicoService picoService,
                      MovesenseService movesenseService,
                      ImuRepository imuRepository,
                      ImuCoordinateRepository imuCoordinateRepository) {
        this.picoService = picoService;
        this.movesenseService = movesenseService;
        this.imuRepository = imuRepository;
        this.imuCoordinateRepository = imuCoordinateRepository;
    }

    public void handleJson(String message) throws JsonProcessingException {
        Imu imu = objectMapper.readValue(message, Imu.class);
        Pico pico = imu.getPico();
        Movesense movesense = imu.getMovesense();

        if (pico != null && !picoService.existsInDatabase(pico)) {
            picoService.save(pico);
        }

        if (movesense != null && !movesenseService.existsInDatabase(movesense)) {
            movesenseService.save(movesense);
        }

        saveIMUData(imu);
        enqueueCoordinates(imu.getImuCoordinates());
    }

    public List<Imu> findAllImu() {
        List<Imu> imuList = imuRepository.findAll();
        for (Imu imu : imuList) {
            imu.setImuCoordinates(
                    imuCoordinateRepository.findByImuId(imu.getId())
            );
        }
        return imuList;
    }

    public Imu findImuById(long id) {
        Imu imu = imuRepository.findById(id);
        if (imu != null) {
            imu.setImuCoordinates(
                    imuCoordinateRepository.findByImuId(id)
            );
        }
        return imu;
    }

    public List<Imu> findImuByTimestampUtcBetween(Long start, Long end) {
        List<Imu> imuList = imuRepository.findByTimestampUtcBetween(start, end);
        for (Imu imu : imuList) {
            imu.setImuCoordinates(
                    imuCoordinateRepository.findByImuId(imu.getId())
            );
        }
        return imuList;
    }

    private void saveIMUData(Imu imu) {
        imuRepository.save(imu);
        LOGGER.info("IMU header saved");
    }

    private void enqueueCoordinates(List<ImuCoordinate> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) return;

        List<ImuCoordinate> toFlush = null;

        synchronized (coordinateBufferLock) {
            coordinateBuffer.addAll(coordinates);

            if (coordinateBuffer.size() >= COORDINATE_BATCH_SIZE) {
                toFlush = new ArrayList<>(coordinateBuffer);
                coordinateBuffer.clear();
            }
        }

        if (toFlush != null) {
            flushCoordinates(toFlush);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void flushCoordinateBufferOnTimer() {
        List<ImuCoordinate> toFlush = null;

        synchronized (coordinateBufferLock) {
            if (!coordinateBuffer.isEmpty()) {
                toFlush = new ArrayList<>(coordinateBuffer);
                coordinateBuffer.clear();
            }
        }

        if (toFlush != null) {
            flushCoordinates(toFlush);
        }
    }

    @Transactional
    protected void flushCoordinates(List<ImuCoordinate> batch) {
        imuCoordinateRepository.saveAll(batch);
        imuCoordinateRepository.flush();
        LOGGER.info("Flushed IMU coordinate batch: {} coordinates", batch.size());
        LOGGER.info("Flushing batch size: {}", batch.size());
    }

    @PreDestroy
    public void shutdownFlush() {
        flushCoordinateBufferOnTimer();
    }
}
