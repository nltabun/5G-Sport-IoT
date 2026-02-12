package org.example.database.repository;

import org.example.database.entity.Gnss;
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
public class GnssRepositoryTest {
    @Autowired
    private PicoRepository picoRepository;

    @Autowired
    private GnssRepository gnssRepository;

    @BeforeEach
    public void setUp() {
        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        picoRepository.save(pico);

        Gnss gnss1 = new Gnss();
        gnss1.setLatitude(37.7749);
        gnss1.setLongitude(-122.4194);
        gnss1.setFixQ(1);
        gnss1.setTimestampUtc(29889L);
        gnss1.setTimestampMs(29889);
        gnss1.setPico(pico);
        gnssRepository.save(gnss1);

        Gnss gnss2 = new Gnss();
        gnss2.setLatitude(-122.4194);
        gnss2.setLongitude(37.7749);
        gnss2.setFixQ(1);
        gnss2.setTimestampUtc(30348L);
        gnss2.setTimestampMs(30348);
        gnss2.setPico(pico);
        gnssRepository.save(gnss2);
    }

    @Test
    public void findByIdTest() {
        Gnss gnss1 = gnssRepository.findById(1);
        Gnss gnss2 = gnssRepository.findById(2);
        Gnss gnss3 = gnssRepository.findById(3);

        assertEquals("e66130100f8c9928", gnss1.getPico().getId(), "Incorrect picoId");
        assertEquals(37.7749, gnss1.getLatitude(), "Incorrect latitude");
        assertEquals(-122.4194, gnss1.getLongitude(), "Incorrect longitude");
        assertEquals(1, gnss1.getFixQ(), "Incorrect FixQ");
        assertEquals(29889L, gnss1.getTimestampUtc(), "Incorrect timestampUtc");
        assertEquals(29889, gnss1.getTimestampMs(), "Incorrect timestampMs");

        assertEquals(-122.4194, gnss2.getLatitude(), "Incorrect latitude");

        assertNull(gnss3, "GNSS with id 3 should not exist");
    }

    @Test
    public void findByTimestampUtcBetweenTest() {
        List<Gnss> gnssList;

        gnssList = gnssRepository.findByTimestampUtcBetween(29890L, 30347L);
        assertEquals(0, gnssList.size(), "Incorrect amount of GNSS objects was found");

        gnssList = gnssRepository.findByTimestampUtcBetween(29889L, 30347L);
        assertEquals(1, gnssList.size(), "Incorrect amount of GNSS objects was found");

        gnssList = gnssRepository.findByTimestampUtcBetween(29890L, 30348L);
        assertEquals(1, gnssList.size(), "Incorrect amount of GNSS objects was found");

        gnssList = gnssRepository.findByTimestampUtcBetween(29889L, 30348L);
        assertEquals(2, gnssList.size(), "Incorrect amount of GNSS objects was found");
    }
}
