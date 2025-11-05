package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PreferenciasDTORespuesta implements Serializable{
    private int idUsuario;
    private List<PreferenciaArtistaDTORespuesta> preferenciasArtistas;
    private List<PreferenciaGeneroDTORespuesta> preferenciasGeneros;
    private List<PreferenciaIdiomaDTORespuesta> preferenciasIdiomas;
}
