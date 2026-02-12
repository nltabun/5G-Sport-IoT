package org.example.database.service;

import org.example.database.entity.Pico;
import org.example.database.repository.PicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PicoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PicoService.class);

    @Autowired
    PicoRepository picoRepository;

    public boolean existsInDatabase(Pico pico) {
        return picoRepository.existsById(pico.getId());
    }

    public void save(Pico pico) {
        picoRepository.saveAll(pico);
        LOGGER.info("Saved {} Pico samples", pico.size());
        // picoRepository.save(pico);
        // LOGGER.info("Raspberry Pi Pico saved to database = '{}'", pico.getId());
    }
}
