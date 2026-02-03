package org.example.database.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.database.entity.Gnss;
import org.example.database.entity.Pico;

import java.io.IOException;

public class GnssDeserializer extends JsonDeserializer<Gnss> {
    @Override
    public Gnss deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode gnssNode = jsonParser.getCodec().readTree(jsonParser);

        Pico pico = new Pico();
        pico.setId(gnssNode.get("Pico_ID").textValue());

        Gnss gnss = new Gnss();
        gnss.setPico(pico);
        gnss.setLatitude(gnssNode.get("Latitude").doubleValue());
        gnss.setLongitude(gnssNode.get("Longitude").doubleValue());

        gnss.setFixQ(gnssNode.path("FixQ").asInt()); // default 0 if missing
        gnss.setTimestampUtc((int)gnssNode.path("Timestamp_UTC").asLong(0)); // handles seconds as number
        gnss.setTimestampMs((int)gnssNode.path("Timestamp_ms").asLong(0));  // default 0 if missing

        return gnss;
    }
}
