package org.example.database.repository;

import org.example.database.entity.HeartRate;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HeartRateRepositoryTest {
    @Autowired
    private PicoRepository picoRepository;

    @Autowired
    private MovesenseRepository movesenseRepository;

    @Autowired
    private HeartRateRepository heartRateRepository;

    @BeforeEach
    public void setUp() {
        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        picoRepository.save(pico);

        Movesense movesense = new Movesense();
        movesense.setId(174630000192L);
        movesenseRepository.save(movesense);

        HeartRate heartRate1 = new HeartRate();
        heartRate1.setAverageBpm(108.7813);
        heartRate1.setTimestampUtc(29889L);
        heartRate1.setTimestampMs(29889);
        heartRate1.setPico(pico);
        heartRate1.setMovesense(movesense);
        heartRateRepository.save(heartRate1);

        HeartRate heartRate2 = new HeartRate();
        heartRate2.setAverageBpm(100);
        heartRate2.setTimestampUtc(30348L);
        heartRate2.setTimestampMs(30348);
        heartRate2.setPico(pico);
        heartRate2.setMovesense(movesense);
        heartRateRepository.save(heartRate2);
    }

    @Test
    public void findByIdTest() {
        HeartRate heartRate1 = heartRateRepository.findById(1);
        HeartRate heartRate2 = heartRateRepository.findById(2);
        HeartRate heartRate3 = heartRateRepository.findById(3);

        assertEquals("e66130100f8c9928", heartRate1.getPico().getId(), "Incorrect picoId");
        assertEquals(174630000192L, heartRate1.getMovesense().getId(), "Incorrect movesenseId");
        assertEquals(108.7813, heartRate1.getAverageBpm(), "Incorrect averageBpm");
        assertEquals(29889L, heartRate1.getTimestampUtc(), "Incorrect timestampUtc");
        assertEquals(29889, heartRate1.getTimestampMs(), "Incorrect timestampMs");

        assertEquals(100, heartRate2.getAverageBpm(), "Incorrect averageBpm");

        assertNull(heartRate3, "Heart rate with id 3 should not exist");
    }

    @Test
    public void findByTimestampUtcBetweenTest() {
        List<HeartRate> heartRateList;

        heartRateList = heartRateRepository.findByTimestampUtcBetween(29890L, 30347L);
        assertEquals(0, heartRateList.size(), "Incorrect amount of heart rate objects was found");

        heartRateList = heartRateRepository.findByTimestampUtcBetween(29889L, 30347L);
        assertEquals(1, heartRateList.size(), "Incorrect amount of heart rate objects was found");

        heartRateList = heartRateRepository.findByTimestampUtcBetween(29890L, 30348L);
        assertEquals(1, heartRateList.size(), "Incorrect amount of heart rate objects was found");

        heartRateList = heartRateRepository.findByTimestampUtcBetween(29889L, 30348L);
        assertEquals(2, heartRateList.size(), "Incorrect amount of heart rate objects was found");
    }
}
