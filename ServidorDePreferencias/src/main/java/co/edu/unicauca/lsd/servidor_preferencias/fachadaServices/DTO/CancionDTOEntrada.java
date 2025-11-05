package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancionDTOEntrada {
    private Integer id;
    private String titulo;
    private String artista;
    private String genero;
    private String idioma;
    @JsonProperty("ruta-almacenamiento")
    private String rutaAlmacenamiento;
}

