package org.example.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Service
public class DatabaseKafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseKafkaConsumer.class);
    private CountDownLatch latch = new CountDownLatch(1); // For testing.
    private String payload; // For testing.

    private final ObjectMapper objectMapper = new ObjectMapper(); // used to unwrap payload

    @Value("${database.enabled}")
    private boolean databaseEnabled;

    @Autowired
    private ImuService imuService;

    @Autowired
    private HeartRateService heartRateService;

    @Autowired
    private EcgService ecgService;

    @Autowired
    private GnssService gnssService;

    @KafkaListener(topics = "#{'${spring.kafka.topics}'.split(',')}", groupId = "${spring.kafka.group-id.database}")
    public void consume(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws IOException {
        //LOGGER.info("Received message from topic '{}', payload = '{}'", topic, message);

        if (databaseEnabled) {
            saveToDatabase(message, topic);
        }

        payload = message;
        latch.countDown();
    }

    private void saveToDatabase(String message, String topic) throws JsonProcessingException {
        String cleanJson = unwrapPayloadIfWrapped(message);
        switch (topic) {
            case "sensors.imu":
                imuService.handleJson(cleanJson);
                break;
            case "sensors.hr":
                heartRateService.handleJson(cleanJson);
                break;
            case "sensors.ecg":
                ecgService.handleJson(cleanJson);
                break;
            case "sensors.gnss":
                gnssService.handleJson(cleanJson);
                break;
        }
    }

    private String unwrapPayloadIfWrapped(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);

            JsonNode payloadNode = root.get("payload");
            if (payloadNode != null && payloadNode.isTextual()) {
                String inner = payloadNode.asText();

                // Validate that inner is valid JSON
                objectMapper.readTree(inner);

                return inner;
            }
        } catch (Exception ignored) {
            // if invalid. Return original message unchanged.
        }
        return message;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getPayload() {
        return payload;
    }
}
