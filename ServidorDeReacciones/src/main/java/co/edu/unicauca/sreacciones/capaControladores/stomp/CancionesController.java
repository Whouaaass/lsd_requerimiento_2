package co.edu.unicauca.sreacciones.capaControladores.stomp;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.UserNotificationDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.ICancionesService;

@Controller
public class CancionesController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CancionesController.class);

    @Autowired
    private ICancionesService cancionesService;

    // Mensajes privados: cliente envía a /apiCancioens/enviarPrivado, backend envía a usuario específico con sendToUser()
    @MessageMapping("/enviar")
    public void enviarMensajePrivado(MessageCancionDTO message, SimpMessageHeaderAccessor headerAccessor) {
        cancionesService.procesarMensaje(message, headerAccessor.getSessionId());
    }

    @MessageMapping("/test/principal") // Client sends to /apiCanciones/test/principal
    public void checkPrincipal(Principal principal) {
        if (principal != null) {
            String principalName = principal.getName();
            logger.info("✅ Message received: Principal name is: {}", principalName);

            // This is where you compare the value!
            if (principalName.equals("JohnDoe")) { // Use a known test user's nickname
                logger.info("🎉 Principal matches the expected user!");
            }

        } else {
            logger.error("❌ Message received: Principal is NULL. User not authenticated.");
        }
    }

    @MessageMapping("/test/user")
    @SendToUser("/queue/notifications")
    public UserNotificationDTO checkUser(Principal principal) {
        if (principal != null) {
            String principalName = principal.getName();
            logger.info("✅ Message received: Principal name is: {}", principalName);

            // This is where you compare the value!
            if (principalName.equals("JohnDoe")) {
                logger.info("🎉 Principal matches the expected user!");
                return new UserNotificationDTO("alert", "Hola, JohnDoe!");
            }

            return new UserNotificationDTO("alert", "Hola, " + principalName + "!");

        } else {
            logger.error("❌ Message received: Principal is NULL. User not authenticated.");
            return new UserNotificationDTO("alert", "No estás autenticado.");
        }        
    }

}
