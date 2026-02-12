package org.example.database.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Gnss;
import org.example.database.entity.Pico;
import org.example.database.repository.GnssRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GnssService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GnssService.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PicoService picoService;

    @Autowired
    GnssRepository gnssRepository;

    public void handleJson(String message) throws JsonProcessingException {
        Gnss gnss = objectMapper.readValue(message, Gnss.class);
        Pico pico = gnss.getPico();

        if (!picoService.existsInDatabase(pico)) {
            picoService.save(pico);
        }

        saveGnssData(gnss);
    }

    public List<Gnss> findAllGnss() {
        return (List<Gnss>) gnssRepository.findAll();
    }

    public Gnss findGnssById(long id) {
        return gnssRepository.findById(id);
    }

    public List<Gnss> findGnssByTimestampUtcBetween(int start, int end) {
        return gnssRepository.findByTimestampUtcBetween(start, end);
    }

    private void saveGnssData(Gnss gnss) {
        gnssRepository.save(gnss);
        LOGGER.info("Saved {} GNSS samples", gnss);
        // gnssRepository.save(gnss);
        // LOGGER.info("GNSS data saved to database = '{}'", gnss);
    }
}
