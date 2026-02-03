package org.example.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WebSocketHandlerTest {
    private WebSocketHandler handler;

    @BeforeEach
    public void resetHandler() {
        handler = new WebSocketHandler();
    }

    @Test
    public void afterConnectionEstablishedTest() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);

        handler.afterConnectionEstablished(session);
        assertEquals(1, handler.getSessions().size(), "There should be 1 session");
    }

    @Test
    public void afterConnectionClosedTest() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        handler.afterConnectionEstablished(session);

        handler.afterConnectionClosed(session, new CloseStatus(1000));
        assertEquals(0, handler.getSessions().size(), "There should be 0 sessions");
    }

    @Test
    public void broadcastTest() throws Exception {
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);
        TextMessage message = new TextMessage("message");
        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        handler.broadcast(message);
        for (WebSocketSession session : handler.getSessions()) {
            verify(session).sendMessage(message);
        }
    }
}
