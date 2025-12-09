package co.edu.unicauca.sreacciones.capaFachadaServices.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaccionPendiente {
    private String token;
    private String usuario;
    private String cancionId;
    private String reaccion; 
}
