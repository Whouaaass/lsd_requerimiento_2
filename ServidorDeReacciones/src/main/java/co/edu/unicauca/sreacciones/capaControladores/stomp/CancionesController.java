package co.edu.unicauca.sreacciones.capaControladores.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;

@Controller
public class CancionesController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Mensajes privados: cliente envía a /apiCancioens/enviarPrivado, backend envía a usuario específico con sendToUser()
    @MessageMapping("/enviar")
    public void enviarMensajePrivado(MessageCancionDTO message) {        
        String destination = "/cancion/" + message.getIdCancion();
        System.out.println("Enviando mensaje \"" + message.getContent() + "\" Al destino " + destination);
        simpMessagingTemplate.convertAndSend(destination, message);
    }

}
