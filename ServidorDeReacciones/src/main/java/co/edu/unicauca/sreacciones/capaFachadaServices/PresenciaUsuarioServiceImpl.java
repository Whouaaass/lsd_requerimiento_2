package co.edu.unicauca.sreacciones.capaFachadaServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sreacciones.capaAccesoADatos.RepositorioClientes;
import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.ChannelNotification;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.DisconnectResult;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.UnsubscriptionResult;


@Service
public class PresenciaUsuarioServiceImpl implements IPresenciaUsuarioService {

    @Autowired
    private RepositorioClientes repositorioClientes;
    
    private static final String UNKNOWN_USER = "Usuario desconocido";

    /**
     * Handler para la conexión del usuario
     */
    @Override
    public String handleUserConnection(String sessionId, String nickname) {
        if (nickname == null) {
            return null;
        }
        
        System.out.println("El usuario " + nickname + " se ha conectado con la sesión: " + sessionId);
        repositorioClientes.addSession(sessionId, nickname);
        
        return nickname + " se ha conectado.";
    }

    /**
     * Handler para la desconexión del usuario
     */
    @Override
    public DisconnectResult handleUserDisconnection(String sessionId) {
        String nickname = repositorioClientes.getNickname(sessionId);
        
        String displayName = nickname != null ? nickname : UNKNOWN_USER;
        System.out.println("El usuario " + displayName + " se ha desconectado de la sesión: " + sessionId);
        
        List<ChannelNotification> channelNotifications = getChannelNotificationsOnDisconnection(sessionId, nickname);
        
        repositorioClientes.removeSession(sessionId);
        
        return new DisconnectResult(displayName, channelNotifications);
    }

    /**
     * Handler para la suscripción del usuario
     */
    @Override
    public MessageCancionDTO handleUserSubscription(String sessionId, String subscriptionId, String destination) {
        if (subscriptionId == null || destination == null) {
            return null;
        }
        
        repositorioClientes.addSubscripcion(sessionId, subscriptionId, destination);
        
        String nickname = repositorioClientes.getNickname(sessionId);
        String displayName = nickname != null ? nickname : UNKNOWN_USER;
        
        System.out.println("El usuario " + displayName + " se ha suscrito a: " + destination);
        
        return createPresenceMessage("connected", null, nickname);
    }

    /**
     * Handler para la desuscripción del usuario
     */
    @Override
    public UnsubscriptionResult handleUserUnsubscription(String sessionId, String subscriptionId) {
        if (subscriptionId == null) {
            return null;
        }
        
        String destination = repositorioClientes.removeSubscripcion(sessionId, subscriptionId);
        
        if (destination != null) {
            String nickname = repositorioClientes.getNickname(sessionId);
            String displayName = nickname != null ? nickname : UNKNOWN_USER;
            
            System.out.println("El usuario " + displayName + " se ha desuscrito de: " + destination);
            
            MessageCancionDTO message = createPresenceMessage("disconnected", null, nickname);
            return new UnsubscriptionResult(destination, message, displayName);
        }
        
        return null;
    }
    
    /**
     * Elimina la sesión del usuario y obtiene todos los destinos donde el
     * usuario se ha suscrito
     * @param sessionId id de sesión del cliente
     * @param nickname nickname del cliente
     * @return notificaciones de desuscripción de los canales
     */
    private List<ChannelNotification> getChannelNotificationsOnDisconnection(String sessionId, String nickname) {
        List<ChannelNotification> notifications = new ArrayList<>();
        Map<String, String> subs = repositorioClientes.getSubscripciones(sessionId);
        
        if (subs != null && nickname != null) {
            for (String destination : subs.values()) {
                if (destination != null) {
                    String message = nickname + " ha salido del canal.";
                    notifications.add(new ChannelNotification(destination, message));
                }
            }
        }
        
        return notifications;
    }
    
    /**
     * Crea un mensaje para el usuario
     * @param type tipo de mensaje
     * @param content contenido del mensaje
     * @param nickname nickname del usuario
     * @return dto del mensaje
     */
    private MessageCancionDTO createPresenceMessage(String type, String content, String nickname) {
        MessageCancionDTO message = new MessageCancionDTO();
        message.setType(type);
        message.setContent(content);
        message.setUserNickname(nickname);
        return message;
    }
}