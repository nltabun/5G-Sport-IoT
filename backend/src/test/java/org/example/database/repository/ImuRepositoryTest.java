package org.example.database.repository;

import org.example.database.entity.Imu;
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
public class ImuRepositoryTest {
    @Autowired
    private PicoRepository picoRepository;

    @Autowired
    private MovesenseRepository movesenseRepository;

    @Autowired
    private ImuRepository imuRepository;

    @BeforeEach
    public void setUp() {
        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        picoRepository.save(pico);

        Movesense movesense = new Movesense();
        movesense.setId(174630000192L);
        movesenseRepository.save(movesense);

        Imu imu1 = new Imu();
        imu1.setTimestampUtc(29889);
        imu1.setTimestampMs(29889);
        imu1.setPico(pico);
        imu1.setMovesense(movesense);
        imuRepository.save(imu1);

        Imu imu2 = new Imu();
        imu2.setTimestampUtc(30348);
        imu2.setTimestampMs(30348);
        imu2.setPico(pico);
        imu2.setMovesense(movesense);
        imuRepository.save(imu2);
    }

    @Test
    public void findByIdTest() {
        Imu imu1 = imuRepository.findById(1);
        Imu imu2 = imuRepository.findById(2);
        Imu imu3 = imuRepository.findById(3);

        assertEquals("e66130100f8c9928", imu1.getPico().getId(), "Incorrect picoId");
        assertEquals(174630000192L, imu1.getMovesense().getId(), "Incorrect movesenseId");
        assertEquals(29889, imu1.getTimestampUtc(), "Incorrect timestampUtc");
        assertEquals(29889, imu1.getTimestampMs(), "Incorrect timestampMs");

        assertEquals(30348, imu2.getTimestampUtc(), "Incorrect timestampUtc");

        assertNull(imu3, "IMU with id 3 should not exist");
    }

    @Test
    public void findByTimestampUtcBetweenTest() {
        List<Imu> imuList;

        imuList = imuRepository.findByTimestampUtcBetween(29890, 30347);
        assertEquals(0, imuList.size(), "Incorrect amount of IMU objects was found");

        imuList = imuRepository.findByTimestampUtcBetween(29889, 30347);
        assertEquals(1, imuList.size(), "Incorrect amount of IMU objects was found");

        imuList = imuRepository.findByTimestampUtcBetween(29890, 30348);
        assertEquals(1, imuList.size(), "Incorrect amount of IMU objects was found");

        imuList = imuRepository.findByTimestampUtcBetween(29889, 30348);
        assertEquals(2, imuList.size(), "Incorrect amount of IMU objects was found");
    }
}
