package co.edu.unicauca.sreacciones.capaFachadaServices;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;

public interface ICancionesService {

    void procesarMensaje(MessageCancionDTO message, String sessionId);
}
