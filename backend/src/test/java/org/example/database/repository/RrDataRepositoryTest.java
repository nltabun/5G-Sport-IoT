package org.example.database.repository;

import org.example.database.entity.HeartRate;
import org.example.database.entity.RrData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=VALUE",
        "spring.test.database.replace=none"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RrDataRepositoryTest {
    @Autowired
    HeartRateRepository heartRateRepository;

    @Autowired
    RrDataRepository rrDataRepository;

    @BeforeEach
    public void setUp() {
        HeartRate heartRate = new HeartRate();
        heartRateRepository.save(heartRate);

        RrData data1 = new RrData();
        data1.setValue(384);
        data1.setHeartRate(heartRate);
        rrDataRepository.save(data1);

        RrData data2 = new RrData();
        data2.setValue(385);
        data2.setHeartRate(heartRate);
        rrDataRepository.save(data2);
    }

    @Test
    public void findByEcgIdTest() {
        List<RrData> data1 = rrDataRepository.findByHeartRateId(1);
        List<RrData> data2 = rrDataRepository.findByHeartRateId(2);

        assertEquals(2, data1.size(), "Incorrect amount of data");
        assertEquals(0, data2.size(), "Incorrect amount of data");
        assertEquals(384, data1.get(0).getValue(), "Data value was incorrect");
        assertEquals(385, data1.get(1).getValue(), "Data value was incorrect");
    }
}
