package org.example.database.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImuSerializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void imuObjectCanBeConvertedToJson() throws IOException {
        Imu imu = createImuObject();
        String actualJson = objectMapper.writeValueAsString(imu);
        String expectedJson = objectMapper.readTree(new File("src/test/java/org/example/mock/data/imu.json")).toString();

        assertEquals(expectedJson, actualJson, "Converted IMU object does not match expected JSON format");
    }

    private Imu createImuObject() {
        Imu imu = new Imu();

        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        imu.setPico(pico);

        Movesense movesense = new Movesense();
        movesense.setId(174630000192L);
        imu.setMovesense(movesense);

        imu.setTimestampUtc(30348L);
        imu.setTimestampMs(29889);

        List<ImuCoordinate> coordinates = new ArrayList<>();

        coordinates.add(createCoordinate("acc", 8.198, -5.47, 1.235));
        coordinates.add(createCoordinate("acc", 8.279, -5.37, 1.278));
        coordinates.add(createCoordinate("gyro", 2.8, 6.3, 5.6));
        coordinates.add(createCoordinate("gyro", 0.56, 9.52, 8.82));
        coordinates.add(createCoordinate("magn", 42.6, -28.5, 3.45));
        coordinates.add(createCoordinate("magn", 44.1, -27.45, 5.7));

        imu.setImuCoordinates(coordinates);

        return imu;
    }

    private ImuCoordinate createCoordinate(String type, double x, double y, double z) {
        ImuCoordinate coordinate = new ImuCoordinate();

        coordinate.setType(type);
        coordinate.setX(x);
        coordinate.setY(y);
        coordinate.setZ(z);

        return coordinate;
    }
}
