package co.edu.unicauca.sreacciones.capaFachadaServices.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.UserNotificationDTO;

@Service
public class ReenvioService {

    private static final Logger logger = LoggerFactory.getLogger(ReenvioService.class);
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    public void reenviarMensajesACancion(MessageCancionDTO message) {
        if (message.getIdCancion() == null) {
            throw new IllegalArgumentException("El mensaje debe de tener un idCanción destino");
        }
        if (message.getType() == null) {
            throw new IllegalArgumentException("El mensaje debe de tener un tipo definido");
        }


        String destination = "/cancion/" + message.getIdCancion();

        logger.info("Enviando el mensaje: \ntipo: {} \n usuario: {} \n id_cancion: {} \n contenido: {} \nAl destino: {}",
                message.getType(),
                message.getUserNickname(),
                message.getIdCancion(),
                message.getContent(),
                destination
        );
        simpMessagingTemplate.convertAndSend(destination, message);
    }

    public void enviarMensajeUsuario(String usuario, String tipo, String contenido) {
        UserNotificationDTO notification = new UserNotificationDTO();
        notification.setType(tipo);
        notification.setContent(contenido);
        simpMessagingTemplate.convertAndSendToUser(usuario, "/queue/notifications", notification);

    }
}
