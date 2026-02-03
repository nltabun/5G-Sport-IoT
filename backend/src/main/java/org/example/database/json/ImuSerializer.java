package org.example.database.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.example.database.entity.Imu;
import org.example.database.entity.ImuCoordinate;

import java.io.IOException;
import java.util.List;

public class ImuSerializer extends JsonSerializer<Imu> {
    @Override
    public void serialize(Imu imu, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("Pico_ID", imu.getPico().getId());
        jsonGenerator.writeNumberField("Movesense_series", imu.getMovesense().getId());
        jsonGenerator.writeNumberField("Timestamp_UTC", imu.getTimestampUtc());
        jsonGenerator.writeNumberField("Timestamp_ms", imu.getTimestampMs());

        jsonGenerator.writeArrayFieldStart("ArrayAcc");
        setCoordinates(jsonGenerator, imu.getImuCoordinates(), "acc");
        jsonGenerator.writeEndArray();

        jsonGenerator.writeArrayFieldStart("ArrayGyro");
        setCoordinates(jsonGenerator, imu.getImuCoordinates(), "gyro");
        jsonGenerator.writeEndArray();

        jsonGenerator.writeArrayFieldStart("ArrayMagn");
        setCoordinates(jsonGenerator, imu.getImuCoordinates(), "magn");
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void setCoordinates(JsonGenerator jsonGenerator, List<ImuCoordinate> coordinates, String type) throws IOException {
        for (ImuCoordinate coordinate : coordinates) {
            if (coordinate.getType().equals(type)) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("x", coordinate.getX());
                jsonGenerator.writeNumberField("y", coordinate.getY());
                jsonGenerator.writeNumberField("z", coordinate.getZ());
                jsonGenerator.writeEndObject();
            }
        }

    }
}
