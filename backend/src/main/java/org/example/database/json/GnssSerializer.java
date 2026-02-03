package org.example.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.example.database.entity.Gnss;

import java.io.IOException;

public class GnssSerializer extends JsonSerializer<Gnss> {
    @Override
    public void serialize(Gnss gnss, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("Pico_ID", gnss.getPico().getId());
        jsonGenerator.writeNumberField("Latitude", gnss.getLatitude());
        jsonGenerator.writeNumberField("Longitude", gnss.getLongitude());
        jsonGenerator.writeNumberField("FixQ", gnss.getFixQ());
        jsonGenerator.writeNumberField("Timestamp_UTC", gnss.getTimestampUtc());
        jsonGenerator.writeNumberField("Timestamp_ms", gnss.getTimestampUtc());

        jsonGenerator.writeEndObject();
    }
}
