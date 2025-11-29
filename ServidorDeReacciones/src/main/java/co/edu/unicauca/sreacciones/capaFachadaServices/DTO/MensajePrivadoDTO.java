package co.edu.unicauca.sreacciones.capaFachadaServices.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MensajePrivadoDTO {
    private String nicknameOrigen;
    private String nicknameDestino;
    private String contenido;
}

