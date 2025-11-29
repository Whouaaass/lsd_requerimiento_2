package co.edu.unicauca.sreacciones.capaFachadaServices.models;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnsubscriptionResult {
    
    private final String destination;
    private final MessageCancionDTO message;
    private final String displayName;
}
