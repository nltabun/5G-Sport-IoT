package org.example.database.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Imu;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImuDeserializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void jsonCanBeConvertedToImuObject() throws IOException {
        Imu imu = objectMapper.readValue(new File("src/test/java/org/example/mock/data/imu.json"), Imu.class);

        assertEquals("e66130100f8c9928", imu.getPico().getId(), "Pico ID was incorrect");
        assertEquals(174630000192L, imu.getMovesense().getId(), "Movesense ID was incorrect");
        assertEquals(30348L, imu.getTimestampUtc(), "UTC timestamp was incorrect");
        assertEquals(29889, imu.getTimestampMs(), "Ms Timestamp was incorrect");

        assertCoordinate(imu, 0, "acc", 8.198, -5.47, 1.235);
        assertCoordinate(imu, 1, "acc", 8.279, -5.37, 1.278);
        assertCoordinate(imu, 2, "gyro", 2.8, 6.3, 5.6);
        assertCoordinate(imu, 3, "gyro", 0.56, 9.52, 8.82);
        assertCoordinate(imu, 4, "magn", 42.6, -28.5, 3.45);
        assertCoordinate(imu, 5, "magn", 44.1, -27.45, 5.7);
    }

    private void assertCoordinate(Imu imu, int index, String type, double x, double y, double z) {
        assertEquals(type, imu.getImuCoordinates().get(index).getType(), "Imu coordinate type was incorrect");
        assertEquals(x, imu.getImuCoordinates().get(index).getX(), "X coordinate was incorrect");
        assertEquals(y, imu.getImuCoordinates().get(index).getY(), "Y coordinate was incorrect");
        assertEquals(z, imu.getImuCoordinates().get(index).getZ(), "Z coordinate was incorrect");
    }
}
