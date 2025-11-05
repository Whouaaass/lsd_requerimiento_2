package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services.componenteComunicacion;

import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.ReproduccionesDTOEntrada;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.*;
import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClienteReproducciones {

    @Value("${reproducciones.service.url:http://localhost:8082}")
    private String URL_REPRO;

    public List<ReproduccionesDTOEntrada> obtenerReproduccionesRemotas(int idUsuario) {
        try {
            URL url = new URL(URL_REPRO + "/api/reproducciones/listar?user_id=" + idUsuario);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(reader, new TypeReference<List<ReproduccionesDTOEntrada>>() {
            });
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo reproducciones remotas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
