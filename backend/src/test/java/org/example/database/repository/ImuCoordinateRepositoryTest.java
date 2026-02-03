package org.example.database.repository;

import org.example.database.entity.Imu;
import org.example.database.entity.ImuCoordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ImuCoordinateRepositoryTest {
    @Autowired
    ImuRepository imuRepository;

    @Autowired
    ImuCoordinateRepository imuCoordinateRepository;

    @BeforeEach
    public void setUp() {
        Imu imu = new Imu();
        imuRepository.save(imu);

        ImuCoordinate coordinate1 = new ImuCoordinate();
        coordinate1.setType("acc");
        coordinate1.setX(8.198);
        coordinate1.setY(-5.47);
        coordinate1.setZ(1.235);
        coordinate1.setImu(imu);
        imuCoordinateRepository.save(coordinate1);

        ImuCoordinate coordinate2 = new ImuCoordinate();
        coordinate2.setType("gyro");
        coordinate2.setX(2.8);
        coordinate2.setY(6.3);
        coordinate2.setZ(5.6);
        coordinate2.setImu(imu);
        imuCoordinateRepository.save(coordinate2);
    }

    @Test
    public void findByEcgIdTest() {
        List<ImuCoordinate> coordinate1 = imuCoordinateRepository.findByImuId(1);
        List<ImuCoordinate> coordinate2 = imuCoordinateRepository.findByImuId(2);

        assertEquals(2, coordinate1.size(), "Incorrect amount of coordinates");
        assertEquals(0, coordinate2.size(), "Incorrect amount of coordinates");
        assertEquals("acc", coordinate1.get(0).getType(), "Incorrect type");
        assertEquals(2.8, coordinate1.get(1).getX(), "Incorrect X value");
    }
}
