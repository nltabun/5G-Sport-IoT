package org.example.database.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.entity.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GnssSerializerTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void gnssObjectCanBeConvertedToJson() throws JsonProcessingException {
        Gnss gnss = createGnssObject();
        String actualJson = objectMapper.writeValueAsString(gnss);
        String expectedJson = "{\"Pico_ID\":\"e66130100f8c9928\"," +
                "\"Latitude\":37.7749," +
                "\"Longitude\":-122.4194," +
                "\"FixQ\":1," +
                "\"Timestamp_UTC\":30348," +
                "\"Timestamp_ms\":30348}";
        assertEquals(expectedJson, actualJson, "Converted GNSS object does not match expected JSON format");
    }

    private Gnss createGnssObject() {
        Gnss gnss = new Gnss();

        Pico pico = new Pico();
        pico.setId("e66130100f8c9928");
        gnss.setPico(pico);

        gnss.setLatitude(37.7749);
        gnss.setLongitude(-122.4194);
        gnss.setFixQ(1);
        gnss.setTimestampUtc(30348);
        gnss.setTimestampMs(29889);

        return gnss;
    }
}
