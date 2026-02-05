package org.example.database.repository;

import org.example.database.entity.Ecg;
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
public class EcgRepositoryTest {
    @Autowired
    private PicoRepository picoRepository;

    @Autowired
    private MovesenseRepository movesenseRepository;

    @Autowired
    private EcgRepository ecgRepository;

    @BeforeEach
    public void setUp() {
        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        picoRepository.save(pico);

        Movesense movesense = new Movesense();
        movesense.setId(174630000192L);
        movesenseRepository.save(movesense);

        Ecg ecg1 = new Ecg();
        ecg1.setTimestampUtc(29889L);
        ecg1.setTimestampMs(29889);
        ecg1.setPico(pico);
        ecg1.setMovesense(movesense);
        ecgRepository.save(ecg1);

        Ecg ecg2 = new Ecg();
        ecg2.setTimestampUtc(30348L);
        ecg2.setTimestampMs(30348);
        ecg2.setPico(pico);
        ecg2.setMovesense(movesense);
        ecgRepository.save(ecg2);
    }

    @Test
    public void findByIdTest() {
        Ecg ecg1 = ecgRepository.findById(1);
        Ecg ecg2 = ecgRepository.findById(2);
        Ecg ecg3 = ecgRepository.findById(3);

        assertEquals("e66130100f8c9928", ecg1.getPico().getId(), "Incorrect picoId");
        assertEquals(174630000192L, ecg1.getMovesense().getId(), "Incorrect movesenseId");
        assertEquals(29889L, ecg1.getTimestampUtc(), "Incorrect timestampUtc");
        assertEquals(29889, ecg1.getTimestampMs(), "Incorrect timestampMs");

        assertEquals(30348L, ecg2.getTimestampUtc(), "Incorrect timestampUtc");

        assertNull(ecg3, "ECG with id 3 should not exist");
    }

    @Test
    public void findByTimestampUtcBetweenTest() {
        List<Ecg> ecgList;

        ecgList = ecgRepository.findByTimestampUtcBetween(29890L, 30347L);
        assertEquals(0, ecgList.size(), "Incorrect amount of Ecg objects was found");

        ecgList = ecgRepository.findByTimestampUtcBetween(29889L, 30347L);
        assertEquals(1, ecgList.size(), "Incorrect amount of Ecg objects was found");

        ecgList = ecgRepository.findByTimestampUtcBetween(29890L, 30348L);
        assertEquals(1, ecgList.size(), "Incorrect amount of Ecg objects was found");

        ecgList = ecgRepository.findByTimestampUtcBetween(29889L, 30348L);
        assertEquals(2, ecgList.size(), "Incorrect amount of Ecg objects was found");
    }
}
