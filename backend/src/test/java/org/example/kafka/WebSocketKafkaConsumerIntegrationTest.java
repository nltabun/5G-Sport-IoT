package org.example.kafka;

import org.example.websocket.WebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { WebSocketKafkaConsumer.class, KafkaProducer.class })
@EnableAutoConfiguration
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class WebSocketKafkaConsumerIntegrationTest {
    @Autowired
    private WebSocketKafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @MockitoBean
    private WebSocketHandler handler;

    private String data = "{\"Name\":\"Test\"}";

    @BeforeEach
    public void reset() {
        consumer.resetLatch();
    }

    private void sendDataAndAwait(String topic) throws InterruptedException {
        producer.send(topic, data);
        consumer.getLatch().await();
    }

    @Test
    public void imuDataIsConsumed() throws InterruptedException {
        sendDataAndAwait("sensors.imu");
        assertEquals(data, consumer.getPayload(), "afterConnectionEstablished method not executed");
    }

    @Test
    public void ecgDataIsConsumed() throws InterruptedException {
        sendDataAndAwait("sensors.ecg");
        assertEquals(data, consumer.getPayload(), "afterConnectionEstablished method not executed");
    }

    @Test
    public void hrDataIsConsumed() throws InterruptedException {
        sendDataAndAwait("sensors.hr");
        assertEquals(data, consumer.getPayload(), "afterConnectionEstablished method not executed");
    }

    @Test
    public void gnssDataIsConsumed() throws InterruptedException {
        sendDataAndAwait("sensors.gnss");
        assertEquals(data, consumer.getPayload(), "afterConnectionEstablished method not executed");
    }

    @Test
    public void broadcastIsCalled() throws InterruptedException, IOException {
        String dataWithTopic = "{\"Name\":\"Test\",\"Topic\":\"sensors.imu\"}";
        sendDataAndAwait("sensors.imu");
        verify(handler, times(1)).broadcast(new TextMessage(dataWithTopic));
    }
}