package co.edu.unicauca.sreacciones.capaAccesoADatos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.sreacciones.capaAccesoADatos.models.ClientSession;

@Repository
public class RepositorioClientes {

    private final ConcurrentHashMap<String, ClientSession> sessionMap;

    public RepositorioClientes() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    public void addSession(String sessionId, String nickname) {
        sessionMap.put(sessionId, new ClientSession(
            nickname,
            new ConcurrentHashMap<>()
        ));
    }

    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    public String getNickname(String sessionId) {
        ClientSession session = sessionMap.get(sessionId);
        return session != null ? session.getNickname() : null;
    }

    public ClientSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void addSubscripcion(String sessionId, String subscriptionId, String destination) {
        ClientSession session = sessionMap.get(sessionId);
        if (session != null) {
            session.addSubscripcion(subscriptionId, destination);
        }
    }

    public String removeSubscripcion(String sessionId, String subscriptionId) {
        ClientSession session = sessionMap.get(sessionId);
        if (session != null) {
            return session.removeSubscripcion(subscriptionId);
        }
        return null;
    }

    public Map<String, String> getSubscripciones(String sessionId) {
        ClientSession session = sessionMap.get(sessionId);
        return session != null ? session.getSubscripciones() : null;
    }
}
