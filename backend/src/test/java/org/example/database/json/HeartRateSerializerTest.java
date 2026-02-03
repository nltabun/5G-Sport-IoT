package org.example.database.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeartRateSerializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void heartRateObjectCanBeConvertedToJson() throws IOException {
        HeartRate heartRate = createHeartRateObject();
        String actualJson = objectMapper.writeValueAsString(heartRate);
        String expectedJson = objectMapper.readTree(new File("src/test/java/org/example/mock/data/heart_rate.json")).toString();

        assertEquals(expectedJson, actualJson, "Converted heart rate object does not match expected JSON format");
    }

    private HeartRate createHeartRateObject() {
        HeartRate heartRate = new HeartRate();

        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        heartRate.setPico(pico);

        Movesense movesense = new Movesense();
        movesense.setId(174630000192L);
        heartRate.setMovesense(movesense);

        heartRate.setAverageBpm(108.7813);
        heartRate.setTimestampUtc(30348);
        heartRate.setTimestampMs(29889);

        List<RrData> rrData = new ArrayList<>();

        int[] rrDataValues = {384};

        for (int value : rrDataValues) {
            RrData data = new RrData();
            data.setValue(value);
            rrData.add(data);
        }

        heartRate.setRrData(rrData);

        return heartRate;
    }
}
