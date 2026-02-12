package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.HeartRate;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;
import org.example.database.entity.RrData;
import org.example.database.repository.HeartRateRepository;
import org.example.database.repository.RrDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartRateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartRateService.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PicoService picoService;

    @Autowired
    MovesenseService movesenseService;

    @Autowired
    HeartRateRepository heartRateRepository;

    @Autowired
    RrDataRepository rrDataRepository;

    public void handleJson(String message) throws JsonProcessingException {
        HeartRate heartRate = objectMapper.readValue(message, HeartRate.class);
        Pico pico = heartRate.getPico();
        Movesense movesense = heartRate.getMovesense();

        if (!picoService.existsInDatabase(pico)) {
            picoService.save(pico);
        }

        if (!movesenseService.existsInDatabase(movesense)) {
            movesenseService.save(movesense);
        }

        saveHeartRateData(heartRate);
        saveRrData(heartRate.getRrData());
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

    public List<HeartRate> findHeartRateByTimestampUtcBetween(int start, int end) {
        List<HeartRate> heartRateList = heartRateRepository.findByTimestampUtcBetween(start, end);
        for (HeartRate heartRate : heartRateList) {
            List<RrData> rrData = rrDataRepository.findByHeartRateId(heartRate.getId());
            heartRate.setRrData(rrData);
        }
        return heartRateList;
    }

    private void saveHeartRateData(HeartRate heartRate) {
        heartRateRepository.save(heartRate);
        LOGGER.info("Saved {} HR samples", heartRate);
        // heartRateRepository.save(heartRate);
        // LOGGER.info("Heart rate data saved to database = '{}'", heartRate);
    }

    private void saveRrData(List<RrData> rrDataArray) {
        rrDataRepository.saveAll(rrDataArray);
        LOGGER.info("Saved {} RR samples", rrDataArray.size());
        // for (RrData rrData : rrDataArray) {
        //     rrDataRepository.save(rrData);
        //     LOGGER.info("rr data saved to database = '{}'", rrData);
        // }
    }
}
