package co.edu.unicauca.sreacciones.capaListeners.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.IPresenciaUsuarioService;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.ChannelNotification;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.DisconnectResult;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.UnsubscriptionResult;

@Component
public class PresenciaUsuarioListener {

    @Autowired
    private IPresenciaUsuarioService presenciaUsuarioService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final String NOTIFICATION_CHANNEL = "/cancion/notificaciones";

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String nickname = accessor.getFirstNativeHeader("nickname");

        String message = presenciaUsuarioService.handleUserConnection(sessionId, nickname);

        if (message != null) {
            simpMessagingTemplate.convertAndSend(NOTIFICATION_CHANNEL, message);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        DisconnectResult result = presenciaUsuarioService.handleUserDisconnection(sessionId);

        String message = result.getDisplayName() + " se ha desconectado.";
        simpMessagingTemplate.convertAndSend(NOTIFICATION_CHANNEL, message);

        for (ChannelNotification notification : result.getChannelNotifications()) {
            simpMessagingTemplate.convertAndSend(notification.getDestination(), notification.getMessage());
        }
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();
        String destination = accessor.getDestination();

        MessageCancionDTO message = presenciaUsuarioService.handleUserSubscription(sessionId, subscriptionId, destination);

        if (message != null && destination != null) {
            simpMessagingTemplate.convertAndSend(destination, message);
        }
    }

    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();

        UnsubscriptionResult result = presenciaUsuarioService.handleUserUnsubscription(sessionId, subscriptionId);

        if (result != null) {
            simpMessagingTemplate.convertAndSend(result.getDestination(), result.getMessage());
        }
    }
}
