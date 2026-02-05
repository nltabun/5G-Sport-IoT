package org.example.database.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.Gnss;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GnssDeserializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void jsonCanBeConvertedToGnssObject() throws IOException {
        Gnss gnss = objectMapper.readValue(new File("src/test/java/org/example/mock/data/gnss.json"), Gnss.class);

        assertEquals("e66130100f8c9928", gnss.getPico().getId(), "Incorrect Pico ID");
        assertEquals(37.7749, gnss.getLatitude(), "Incorrect latitude");
        assertEquals(-122.4194, gnss.getLongitude(), "Incorrect longitude");
        assertEquals(1, gnss.getFixQ(), "Incorrect FixQ");
        assertEquals(30348L, gnss.getTimestampUtc(), "Incorrect UTC timestamp");
        assertEquals(29889, gnss.getTimestampMs(), "Incorrect ms timestamp");
    }
}
