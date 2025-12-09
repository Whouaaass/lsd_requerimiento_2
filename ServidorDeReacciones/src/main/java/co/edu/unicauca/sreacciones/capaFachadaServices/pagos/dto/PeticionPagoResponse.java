package co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto;
import lombok.Data;

@Data
public class PeticionPagoResponse {
    private Boolean exito;
    private String mensaje;
}
