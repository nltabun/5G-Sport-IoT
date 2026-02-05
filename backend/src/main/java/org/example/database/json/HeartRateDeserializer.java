package org.example.database.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.database.entity.*;

import java.io.IOException;
import java.util.Iterator;

public class HeartRateDeserializer extends JsonDeserializer<HeartRate> {
    @Override
    public HeartRate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        HeartRate heartRate = new HeartRate();
        heartRate.setAverageBpm(node.get("Average_BPM").doubleValue());
        heartRate.setTimestampUtc(node.get("Timestamp_UTC").longValue());
        heartRate.setTimestampMs(node.get("Timestamp_ms").intValue());

        Pico pico = new Pico();
        pico.setId(node.get("Pico_ID").textValue());
        heartRate.setPico(pico);

        Movesense movesense = new Movesense();
        movesense.setId(node.get("Movesense_series").longValue());
        heartRate.setMovesense(movesense);

        Iterator<JsonNode> rrDataList = node.get("rrData").values();
        while(rrDataList.hasNext()) {
            RrData rrData = new RrData();
            rrData.setValue(rrDataList.next().intValue());
            rrData.setHeartRate(heartRate);
            heartRate.addRrData(rrData);
        }

        return heartRate;
    }
}
