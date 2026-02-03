package org.example.kafka;

import org.example.database.service.EcgService;
import org.example.database.service.GnssService;
import org.example.database.service.HeartRateService;
import org.example.database.service.ImuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { DatabaseKafkaConsumer.class, KafkaProducer.class })
@EnableAutoConfiguration
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class DatabaseKafkaIntegrationTest {
    @Autowired
    private DatabaseKafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @MockitoBean
    private ImuService imuService;

    @MockitoBean
    private HeartRateService heartRateService;

    @MockitoBean
    private EcgService ecgService;

    @MockitoBean
    private GnssService gnssService;

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
}
