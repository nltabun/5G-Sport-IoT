package org.example.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.example.database.entity.HeartRate;
import org.example.database.entity.RrData;

import java.io.IOException;

public class HeartRateSerializer extends JsonSerializer<HeartRate> {
    @Override
    public void serialize(HeartRate heartRate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("Pico_ID", heartRate.getPico().getId());
        jsonGenerator.writeNumberField("Movesense_series", heartRate.getMovesense().getId());
        jsonGenerator.writeNumberField("Average_BPM", heartRate.getAverageBpm());
        jsonGenerator.writeNumberField("Timestamp_UTC", heartRate.getTimestampUtc());
        jsonGenerator.writeNumberField("Timestamp_ms", heartRate.getTimestampMs());

        jsonGenerator.writeArrayFieldStart("rrData");
        for (RrData rrData : heartRate.getRrData()) {
            jsonGenerator.writeNumber(rrData.getValue());
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}
