package org.example.database.repository;

import org.example.database.entity.Ecg;
import org.example.database.entity.EcgSample;
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
public class EcgSampleRepositoryTest {

    @Autowired
    EcgRepository ecgRepository;

    @Autowired
    EcgSampleRepository ecgSampleRepository;

    @BeforeEach
    public void setUp() {
        Ecg ecg = new Ecg();
        ecgRepository.save(ecg);

        EcgSample sample1 = new EcgSample();
        sample1.setValue(-62342);
        sample1.setEcg(ecg);
        ecgSampleRepository.save(sample1);

        EcgSample sample2 = new EcgSample();
        sample2.setValue(-51680);
        sample2.setEcg(ecg);
        ecgSampleRepository.save(sample2);
    }

    @Test
    public void findByEcgIdTest() {
        List<EcgSample> samples1 = ecgSampleRepository.findByEcgId(1);
        List<EcgSample> samples2 = ecgSampleRepository.findByEcgId(2);

        assertEquals(2, samples1.size(), "Sample list should have two samples");
        assertEquals(0, samples2.size(), "Sample list should be empty");
        assertEquals(-62342, samples1.get(0).getValue(), "Sample value was incorrect");
        assertEquals(-51680, samples1.get(1).getValue(), "Sample value was incorrect");
    }
}
