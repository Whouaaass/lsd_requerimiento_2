package co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeticionPagoDTO {
    private String token;
    private String usuario;
    private String cancionId;
    private int valor;
}
