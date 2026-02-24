package org.example.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Service
public class WebSocketKafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketKafkaConsumer.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private CountDownLatch latch = new CountDownLatch(1); // For testing.
    private String payload; // For testing.

    @Autowired
    private WebSocketHandler handler;

    @KafkaListener(topics = "#{'${spring.kafka.topics}'.split(',')}", groupId = "${spring.kafka.group-id.websocket}")
    public void consume(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws IOException {
        LOGGER.info("Received message from topic '{}', payload = '{}'", topic, message);

        broadcast(message, topic);

        payload = message;
        latch.countDown();
    }

    private void broadcast(String message, String topic) throws IOException {
        message = addTopicToMessage(message, topic);
        TextMessage convertedMessage = new TextMessage(message);
        handler.broadcast(convertedMessage);
    }

    private String addTopicToMessage(String message, String topic) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            ((ObjectNode)jsonNode).put("Topic", topic);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException exception) {
            LOGGER.error("Message could not be processed as JSON: '{}'", exception.getMessage());
            return message;
        }
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
