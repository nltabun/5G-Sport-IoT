package org.example.database.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.database.entity.Imu;
import org.example.database.entity.ImuCoordinate;
import org.example.database.entity.Movesense;
import org.example.database.entity.Pico;

import java.io.IOException;
import java.util.Iterator;

public class ImuDeserializer extends JsonDeserializer<Imu> {
    @Override
    public Imu deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Imu imu = new Imu();
        imu.setTimestampUtc(node.get("Timestamp_UTC").intValue());
        imu.setTimestampMs(node.get("Timestamp_ms").intValue());

        Pico pico = new Pico();
        pico.setId(node.get("Pico_ID").textValue());
        imu.setPico(pico);

        Movesense movesense = new Movesense();
        movesense.setId(node.get("Movesense_series").longValue());
        imu.setMovesense(movesense);

        Iterator<JsonNode> ArrayAcc = node.get("ArrayAcc").values();
        while (ArrayAcc.hasNext()) {
            ImuCoordinate coordinate = new ImuCoordinate();
            coordinate.setImu(imu);
            coordinate.setType("acc");
            setXYZ(coordinate, ArrayAcc.next());
            imu.addImuCoordinate(coordinate);
        }

        Iterator<JsonNode> ArrayGyro = node.get("ArrayGyro").values();
        while (ArrayGyro.hasNext()) {
            ImuCoordinate coordinate = new ImuCoordinate();
            coordinate.setImu(imu);
            coordinate.setType("gyro");
            setXYZ(coordinate, ArrayGyro.next());
            imu.addImuCoordinate(coordinate);
        }

        Iterator<JsonNode> ArrayMagn = node.get("ArrayMagn").values();
        while (ArrayMagn.hasNext()) {
            ImuCoordinate coordinate = new ImuCoordinate();
            coordinate.setImu(imu);
            coordinate.setType("magn");
            setXYZ(coordinate, ArrayMagn.next());
            imu.addImuCoordinate(coordinate);
        }

        return imu;
    }

    private void setXYZ(ImuCoordinate coordinate, JsonNode node) {
        coordinate.setX(node.get("x").doubleValue());
        coordinate.setY(node.get("y").doubleValue());
        coordinate.setZ(node.get("z").doubleValue());
    }
}
