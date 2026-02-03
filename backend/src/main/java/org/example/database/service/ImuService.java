package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Imu;
import org.example.database.entity.ImuCoordinate;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;
import org.example.database.repository.ImuCoordinateRepository;
import org.example.database.repository.ImuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImuService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImuService.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PicoService picoService;

    @Autowired
    MovesenseService movesenseService;

    @Autowired
    ImuRepository imuRepository;

    @Autowired
    ImuCoordinateRepository imuCoordinateRepository;

    public void handleJson(String message) throws JsonProcessingException {
        Imu imu = objectMapper.readValue(message, Imu.class);
        Pico pico = imu.getPico();
        Movesense movesense = imu.getMovesense();

        if (!picoService.existsInDatabase(pico)) {
            picoService.save(pico);
        }

        if (!movesenseService.existsInDatabase(movesense)) {
            movesenseService.save(movesense);
        }

        saveIMUData(imu);
        saveIMUCoordinates(imu.getImuCoordinates());
    }

    public List<Imu> findAllImu() {
        List<Imu> imuList = (List<Imu>) imuRepository.findAll();
        for (Imu imu : imuList) {
            List<ImuCoordinate> coordinates = imuCoordinateRepository.findByImuId(imu.getId());
            imu.setImuCoordinates(coordinates);
        }
        return imuList;
    }

    public Imu findImuById(long id) {
        Imu imu = imuRepository.findById(id);
        if (imu != null) {
            List<ImuCoordinate> coordinates = imuCoordinateRepository.findByImuId(id);
            imu.setImuCoordinates(coordinates);
        }
        return imu;
    }

    public List<Imu> findImuByTimestampUtcBetween(int start, int end) {
        List<Imu> imuList = imuRepository.findByTimestampUtcBetween(start, end);
        for (Imu imu : imuList) {
            List<ImuCoordinate> coordinates = imuCoordinateRepository.findByImuId(imu.getId());
            imu.setImuCoordinates(coordinates);
        }
        return imuList;
    }

    private void saveIMUData(Imu imu) {
        imuRepository.save(imu);
        LOGGER.info("IMU data saved to database = '{}'", imu);
    }

    private void saveIMUCoordinates(List<ImuCoordinate> coordinates) {
        for (ImuCoordinate coordinate : coordinates) {
            imuCoordinateRepository.save(coordinate);
            LOGGER.info("IMU coordinate saved to database = '{}'", coordinate);
        }
    }
}
