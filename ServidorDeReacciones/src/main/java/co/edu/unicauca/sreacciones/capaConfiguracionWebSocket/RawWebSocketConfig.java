package co.edu.unicauca.sreacciones.capaConfiguracionWebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Configuración para una conexión de websocket pura
 */
@Configuration
@EnableWebSocket
public class RawWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private TextWebSocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        if (this.handler != null) {
            registry.addHandler(this.handler, "/raw-ws")
                    .setAllowedOriginPatterns("*");
        }
    }
}
