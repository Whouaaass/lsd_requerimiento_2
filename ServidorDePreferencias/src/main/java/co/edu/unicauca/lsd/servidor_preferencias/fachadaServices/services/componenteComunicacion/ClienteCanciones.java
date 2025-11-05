package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services.componenteComunicacion;

import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.CancionDTOEntrada;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.*;
import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClienteCanciones {

    @Value("${canciones.service.url:http://localhost:5000}")
    private String URL_CANCIONES; // micro de canciones 

    public List<CancionDTOEntrada> obtenerCancionesRemotas() {
        try {
            URL url = new URL(URL_CANCIONES + "/canciones/listar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(reader, new TypeReference<List<CancionDTOEntrada>>() {
            });
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo canciones remotas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
