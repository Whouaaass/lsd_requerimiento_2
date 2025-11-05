package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaIdiomaDTORespuesta implements Serializable {
    private String nombreIdioma;
    private Integer numeroPreferencias;
}

