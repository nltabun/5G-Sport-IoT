package org.example.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CountDownLatch;

public class ClientWebSocketHandler extends TextWebSocketHandler {
    private CountDownLatch latch = new CountDownLatch(1);
    private String state;
    private String payload;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        state = "Connection established";
        latch.countDown();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        state = "Connection closed";
        latch.countDown();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        state = "Message received";
        payload = message.getPayload();
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getState() {
        return state;
    }

    public String getPayload() {
        return payload;
    }
}
