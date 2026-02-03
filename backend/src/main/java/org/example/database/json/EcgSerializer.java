package org.example.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.example.database.entity.Ecg;
import org.example.database.entity.EcgSample;

import java.io.IOException;

public class EcgSerializer extends JsonSerializer<Ecg> {
    @Override
    public void serialize(Ecg ecg, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("Pico_ID", ecg.getPico().getId());
        jsonGenerator.writeNumberField("Movesense_series", ecg.getMovesense().getId());
        jsonGenerator.writeNumberField("Timestamp_UTC", ecg.getTimestampUtc());
        jsonGenerator.writeNumberField("Timestamp_ms", ecg.getTimestampMs());

        jsonGenerator.writeArrayFieldStart("Samples");
        for (EcgSample sample : ecg.getEcgSamples()) {
            jsonGenerator.writeNumber(sample.getValue());
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}
