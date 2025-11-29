package co.edu.unicauca.sreacciones.capaListeners.ws;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This handler is not fully implemented, it was a test for raw websockets
 */
@Component
public class WsHandler extends TextWebSocketHandler {

    @Autowired
    private ChannelRegistry registry;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connected " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> json = mapper.readValue(message.getPayload(), Map.class);

        String type = (String) json.get("type");

        switch (type) {
            case "subscribe":
                handleSubscribe(json, session);
                break;

            case "publish":
                handlePublish(json);
                break;
        }
    }

    private void handleSubscribe(Map<String, Object> json, WebSocketSession session) throws Exception {
        String channel = (String) json.get("channel");
        registry.subscribe(channel, session);

        System.out.println("Session " + session.getId() + " subscribed to " + channel);
    }

    private void handlePublish(Map<String, Object> json) throws Exception {
        String channel = (String) json.get("channel");
        Object data = json.get("data");

        System.out.println("Publishing to " + channel);

        for (WebSocketSession s : registry.getSubscribers(channel)) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(mapper.writeValueAsString(data)));
            }
        }
    }

    @Override    
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.unsubscribeAll(session);
        System.out.println("Disconnected " + session.getId());
    }

}
