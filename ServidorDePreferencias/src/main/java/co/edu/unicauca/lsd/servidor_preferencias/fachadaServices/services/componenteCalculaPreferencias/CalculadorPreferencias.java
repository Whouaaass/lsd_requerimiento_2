package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services.componenteCalculaPreferencias;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.CancionDTOEntrada;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.PreferenciaArtistaDTORespuesta;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.PreferenciaGeneroDTORespuesta;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.PreferenciaIdiomaDTORespuesta;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.ReproduccionesDTOEntrada;

@Component
public class CalculadorPreferencias {

    public PreferenciasDTORespuesta calcular(Integer idUsuario,
                                              List<CancionDTOEntrada> canciones,
                                              List<ReproduccionesDTOEntrada> reproducciones) {
        // Crear un mapa de canciones por ID
        Map<Integer, CancionDTOEntrada> mapaCanciones = canciones.stream()
            .filter(Objects::nonNull)
            .filter(c -> c.getId() != null)
            .collect(Collectors.toMap(CancionDTOEntrada::getId, c -> c, (a,b) -> a));

        // Contadores de preferencias
        Map<String, Integer> contadorGeneros = new HashMap<>();
        Map<String, Integer> contadorArtistas = new HashMap<>();
        Map<String, Integer> contadorIdiomas = new HashMap<>();

        // mostrar cuántas canciones y reproducciones llegaron
        System.out.println("Canciones recibidas: " + mapaCanciones.size());
        System.out.println("Reproducciones recibidas: " + reproducciones.size());

        // Recorrer reproducciones
        for (ReproduccionesDTOEntrada r : reproducciones) {
            Integer idCancion = r.getIdCancion();
            if (idCancion == null) {
                System.out.println("⚠️ Reproducción sin idCancion, se omite.");
                continue;
            }

            // Conversión segura: si hay mezcla Long/Integer
            CancionDTOEntrada c = mapaCanciones.get(idCancion.intValue());

           
            System.out.println("Procesando reproducción de canción ID: " + idCancion);
            System.out.println("Canción encontrada: " + (c != null ? c.getTitulo() : " No encontrada"));

            if (c == null) continue;

            String genero = (c.getGenero() != null) ? c.getGenero() : "Desconocido";
            String artista = (c.getArtista() != null) ? c.getArtista() : "Desconocido";
            String idioma = (c.getIdioma() != null) ? c.getIdioma() : "Desconocido";

            int veces = (r.getReproducciones() != null) ? r.getReproducciones() : 1;
            

            contadorGeneros.put(genero, contadorGeneros.getOrDefault(genero, 0) + veces);
            contadorArtistas.put(artista, contadorArtistas.getOrDefault(artista, 0) + veces);
            contadorIdiomas.put(idioma, contadorIdiomas.getOrDefault(idioma, 0) + veces);

        }

        // Convertir contadores a listas ordenadas
        List<PreferenciaGeneroDTORespuesta> preferenciasGeneros = contadorGeneros.entrySet().stream()
            .map(e -> new PreferenciaGeneroDTORespuesta(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingInt(PreferenciaGeneroDTORespuesta::getNumeroPreferencias).reversed())
            .collect(Collectors.toList());

        List<PreferenciaArtistaDTORespuesta> preferenciasArtistas = contadorArtistas.entrySet().stream()
            .map(e -> new PreferenciaArtistaDTORespuesta(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingInt(PreferenciaArtistaDTORespuesta::getNumeroPreferencias).reversed())
            .collect(Collectors.toList());

        List<PreferenciaIdiomaDTORespuesta> preferenciasIdiomas = contadorIdiomas.entrySet().stream()
            .map(e -> new PreferenciaIdiomaDTORespuesta(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingInt(PreferenciaIdiomaDTORespuesta::getNumeroPreferencias).reversed())
            .collect(Collectors.toList());

        // Armar la respuesta final
        PreferenciasDTORespuesta respuesta = new PreferenciasDTORespuesta();
        respuesta.setIdUsuario(idUsuario);
        respuesta.setPreferenciasGeneros(preferenciasGeneros);
        respuesta.setPreferenciasArtistas(preferenciasArtistas);
        respuesta.setPreferenciasIdiomas(preferenciasIdiomas);

        
        System.out.println("✅ Preferencias calculadas para usuario: " + idUsuario);
        System.out.println("   Géneros: " + preferenciasGeneros.size());
        System.out.println("   Artistas: " + preferenciasArtistas.size());
        System.out.println("   Idiomas: " + preferenciasIdiomas.size());

        return respuesta;
    }
}
