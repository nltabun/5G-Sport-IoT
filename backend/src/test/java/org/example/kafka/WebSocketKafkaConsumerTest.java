package org.example.kafka;

import org.example.websocket.WebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebSocketKafkaConsumerTest {

    @Mock
    private WebSocketHandler webSocketHandler;

    @InjectMocks
    private WebSocketKafkaConsumer kafkaConsumer = new WebSocketKafkaConsumer();

    private String message = "{\"Name\":\"Test\"}";

    @Test
    public void topicIsAddedToHeartRateData() throws IOException {
        kafkaConsumer.consume(message, "sensors.hr");
        verify(webSocketHandler).broadcast(new TextMessage("{\"Name\":\"Test\",\"Topic\":\"sensors.hr\"}"));
    }

    @Test
    public void topicIsAddedToImuData() throws IOException {
        kafkaConsumer.consume(message, "sensors.imu");
        verify(webSocketHandler).broadcast(new TextMessage("{\"Name\":\"Test\",\"Topic\":\"sensors.imu\"}"));
    }

    @Test
    public void topicIsAddedToEcgData() throws IOException {
        kafkaConsumer.consume(message, "sensors.ecg");
        verify(webSocketHandler).broadcast(new TextMessage("{\"Name\":\"Test\",\"Topic\":\"sensors.ecg\"}"));
    }

    @Test
    public void topicIsAddedToGnssData() throws IOException {
        kafkaConsumer.consume(message, "sensors.gnss");
        verify(webSocketHandler).broadcast(new TextMessage("{\"Name\":\"Test\",\"Topic\":\"sensors.gnss\"}"));
    }
}
