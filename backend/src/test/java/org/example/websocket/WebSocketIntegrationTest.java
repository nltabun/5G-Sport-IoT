package org.example.websocket;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@SpringBootTest(classes = {WebSocketConfig.class, WebSocketHandler.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class WebSocketIntegrationTest {
    @Autowired
    private WebSocketHandler serverHandler;

    @LocalServerPort
    private int port;

    private final WebSocketClient client = new StandardWebSocketClient();
    private final ClientWebSocketHandler clientHandler = new ClientWebSocketHandler();
    private WebSocketSession session;

    @BeforeEach
    public void reset() {
        clientHandler.resetLatch();
    }

    private void connect() {
        CompletableFuture<WebSocketSession> sessionFuture = client.execute(clientHandler, "ws://localhost:" + port + "/");
        session = sessionFuture.join();
    }

    private void disconnect() throws IOException {
        session.close();
    }

    @Test
    @Timeout(10)
    public void connectionCanBeEstablished() throws IOException, InterruptedException {
        connect();
        clientHandler.getLatch().await();
        assertEquals("Connection established", clientHandler.getState(), "afterConnectionEstablished method not executed");
        disconnect();
    }

    @Test
    @Timeout(10)
    public void connectionCanBeClosed() throws IOException, InterruptedException {
        connect();
        clientHandler.getLatch().await();
        disconnect();
        assertEquals("Connection closed", clientHandler.getState(), "afterConnectionClosed method not executed");
    }

    @Test
    @Timeout(10)
    public void broadcastTest() throws IOException, InterruptedException {
        connect();
        clientHandler.getLatch().await();
        clientHandler.resetLatch();

        String data = "test";
        serverHandler.broadcast(new TextMessage(data));
        clientHandler.getLatch().await();

        assertEquals("Message received", clientHandler.getState(), "handleTextMessage method not executed");
        assertEquals(data, clientHandler.getPayload(), "Received incorrect data");

        disconnect();
    }
}