package org.example.database.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Ecg;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EcgDeserializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void jsonCanBeConvertedToEcgObject() throws IOException {
        Ecg ecg = objectMapper.readValue(new File("src/test/java/org/example/mock/data/ecg.json"), Ecg.class);

        assertEquals("e66130100f8c9928", ecg.getPico().getId(), "Pico ID was incorrect");
        assertEquals(174630000192L, ecg.getMovesense().getId(), "Movesense ID was incorrect");
        assertEquals(30348L, ecg.getTimestampUtc(), "UTC timestamp was incorrect");
        assertEquals(29889, ecg.getTimestampMs(), "Ms Timestamp was incorrect");

        int[] expectedSampleValues = {-62342, -51680, -43311, -35942, -29149, -23343, -18437, -14248, -10712, -7628, -4838, -2323,
                -90, 1953, 3926, 5939};
        int[] actualSampleValues = getSampleValuesAsArray(ecg);

        assertArrayEquals(expectedSampleValues, actualSampleValues, "Ecg samples were incorrect");
    }

    private int[] getSampleValuesAsArray(Ecg ecg) {
        int sampleAmount = ecg.getEcgSamples().size();
        int[] sampleValuesArray = new int[sampleAmount];

        for (int i = 0; i < sampleAmount; i++) {
            sampleValuesArray[i] = ecg.getEcgSamples().get(i).getValue();
        }

        return sampleValuesArray;
    }
}
