package org.example.database.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.HeartRate;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeartRateDeserializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void jsonCanBeConvertedToHeartRateObject() throws IOException {
        HeartRate heartRate = objectMapper.readValue(new File("src/test/java/org/example/mock/data/heart_rate.json"), HeartRate.class);

        assertEquals("e66130100f8c9928", heartRate.getPico().getId(), "Pico ID was incorrect");
        assertEquals(174630000192L, heartRate.getMovesense().getId(), "Movesense ID was incorrect");
        assertEquals(108.7813, heartRate.getAverageBpm(), "Average BPM was incorrect");
        assertEquals(30348L, heartRate.getTimestampUtc(), "UTC timestamp was incorrect");
        assertEquals(29889, heartRate.getTimestampMs(), "Ms Timestamp was incorrect");

        int[] expectedRrDataValues = {384};
        int[] actualRrDataValues = getRrDataValuesAsArray(heartRate);

        assertArrayEquals(expectedRrDataValues, actualRrDataValues, "Ecg samples were incorrect");
    }

    private int[] getRrDataValuesAsArray(HeartRate heartRate) {
        int rrDataAmount = heartRate.getRrData().size();
        int[] rrDataArray = new int[rrDataAmount];

        for (int i = 0; i < rrDataAmount; i++) {
            rrDataArray[i] = heartRate.getRrData().get(i).getValue();
        }

        return rrDataArray;
    }
}
