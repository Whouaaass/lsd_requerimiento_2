package co.edu.unicauca.sreacciones.capaFachadaServices;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.DisconnectResult;
import co.edu.unicauca.sreacciones.capaFachadaServices.models.UnsubscriptionResult;

public interface IPresenciaUsuarioService {

    String handleUserConnection(String sessionId, String nickname);

    DisconnectResult handleUserDisconnection(String sessionId);

    MessageCancionDTO handleUserSubscription(String sessionId, String subscriptionId, String destination);

    UnsubscriptionResult handleUserUnsubscription(String sessionId, String subscriptionId);

}
