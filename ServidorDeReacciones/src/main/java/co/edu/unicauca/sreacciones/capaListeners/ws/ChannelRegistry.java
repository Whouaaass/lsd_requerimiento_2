package co.edu.unicauca.sreacciones.capaListeners.ws;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ChannelRegistry {

    // channel -> set of sessions
    private final ConcurrentHashMap<String, Set<WebSocketSession>> channels = new ConcurrentHashMap<>();

    public void subscribe(String channel, WebSocketSession session) {
        channels.computeIfAbsent(channel, c -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    public void unsubscribeAll(WebSocketSession session) {
        channels.values().forEach(set -> set.remove(session));
    }

    public Set<WebSocketSession> getSubscribers(String channel) {
        return channels.getOrDefault(channel, Set.of());
    }
}

