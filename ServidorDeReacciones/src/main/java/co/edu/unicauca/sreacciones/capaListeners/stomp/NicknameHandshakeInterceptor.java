package co.edu.unicauca.sreacciones.capaListeners.stomp;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class NicknameHandshakeInterceptor implements HandshakeInterceptor {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NicknameHandshakeInterceptor.class);

    // This method is called before the WebSocket connection is established
    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes) throws Exception {

        // --- Debugging Code ---
        String nickname = request.getHeaders().getFirst("nickname");
        logger.info("✅ WS Handshake: Nickname header received: {}", nickname);
        // ----------------------

        if (nickname != null && !nickname.trim().isEmpty()) {
            Principal principal = () -> nickname;

            // This is the key attribute for Spring to recognize the Principal
            attributes.put("SPRING_SECURITY_CONTEXT", principal);

            // --- Additional Debugging ---
            logger.info("✅ WS Handshake: Principal successfully set as: {}", principal.getName());
            // ----------------------------
        } else {
            logger.warn("⚠️ WS Handshake: 'nickname' header is missing or empty. User will be anonymous.");
        }
        
        return true;
    }

    // This method is called after the handshake is complete
    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception) {
        // No action needed here for this purpose
    }
}
