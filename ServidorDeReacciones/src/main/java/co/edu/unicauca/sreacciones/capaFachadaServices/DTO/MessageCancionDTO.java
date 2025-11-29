package co.edu.unicauca.sreacciones.capaFachadaServices.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageCancionDTO {

    private String type;
    private String content;
    private Integer idCancion;
    private String userNickname;
}
