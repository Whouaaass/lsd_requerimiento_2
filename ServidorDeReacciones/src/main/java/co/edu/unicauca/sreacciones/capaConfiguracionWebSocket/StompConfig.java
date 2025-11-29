package co.edu.unicauca.sreacciones.capaConfiguracionWebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración para una conexión con websocket sobre stomp
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {   
    config.enableSimpleBroker("/cancion"); 
    config.setApplicationDestinationPrefixes("/apiCanciones");    
  }

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
      registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
      registry.addEndpoint("/ws-raw")
            .setAllowedOriginPatterns("*");            
  }
}
