package co.edu.unicauca.sreacciones.capaFachadaServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationDTO {
    private String type;
    private String content;
}
