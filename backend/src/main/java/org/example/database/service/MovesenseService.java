package org.example.database.service;

import org.example.database.entity.Movesense;
import org.example.database.repository.MovesenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovesenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovesenseService.class);

    @Autowired
    MovesenseRepository movesenseRepository;

    public boolean existsInDatabase(Movesense movesense) {
        return movesenseRepository.existsById(movesense.getId());
    }

    public void save(Movesense movesense) {
        movesenseRepository.save(movesense);
        LOGGER.info("Saved {} MOVESENSE samples", movesense);
        // movesenseRepository.save(movesense);
        // LOGGER.info("Movesense saved to database = '{}'", movesense.getId());
    }
}
