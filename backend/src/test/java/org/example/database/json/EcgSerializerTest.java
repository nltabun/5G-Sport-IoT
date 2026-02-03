package org.example.database.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Ecg;
import org.example.database.entity.EcgSample;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EcgSerializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void ecgObjectCanBeConvertedToJson() throws IOException {
        Ecg ecg = createEcgObject();
        String actualJson = objectMapper.writeValueAsString(ecg);
        String expectedJson = objectMapper.readTree(new File("src/test/java/org/example/mock/data/ecg.json")).toString();

        assertEquals(expectedJson, actualJson, "Converted ECG object does not match expected JSON format");
    }

    private Ecg createEcgObject() {
        Ecg ecg = new Ecg();

        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        ecg.setPico(pico);

        Movesense movesense = new Movesense();
        movesense.setId(174630000192L);
        ecg.setMovesense(movesense);

        ecg.setTimestampUtc(30348);
        ecg.setTimestampMs(29889);

        List<EcgSample> samples = new ArrayList<>();

        int[] sampleValues = {-62342, -51680, -43311, -35942, -29149, -23343, -18437, -14248, -10712, -7628, -4838,
                -2323, -90, 1953, 3926, 5939};

        for (int value : sampleValues) {
            EcgSample sample = new EcgSample();
            sample.setValue(value);
            samples.add(sample);
        }

        ecg.setEcgSamples(samples);

        return ecg;
    }
}
