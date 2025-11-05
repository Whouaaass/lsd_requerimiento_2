package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReproduccionesDTOEntrada {

    private Integer idUsuario;

    // Para cuando venga directamente en el JSON
    @JsonProperty("idCancion")
    private Integer idCancion;

    // Para cuando venga anidado dentro de "cancion" el idcancion
    private CancionDTO cancion;

    private Integer reproducciones;

    // Método auxiliar que garantiza obtener el idCancion de donde venga
    public Integer getIdCancion() {
        if (idCancion != null) {
            return idCancion;
        }
        return (cancion != null) ? cancion.getId() : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CancionDTO {
        private Integer id;
        private String artista;
        private String genero;
        private String idioma;
    }
}
