package co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.CancionDTOEntrada;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.ReproduccionesDTOEntrada;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services.componenteCalculaPreferencias.CalculadorPreferencias;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services.componenteComunicacion.ClienteCanciones;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services.componenteComunicacion.ClienteReproducciones;

@Service
public class PreferenciasServiceImpl implements IPreferenciasService {

    // mantener clientes
    @Autowired
    private ClienteReproducciones clienteReproducciones;
    @Autowired
    private ClienteCanciones clienteCanciones;
    @Autowired
    private CalculadorPreferencias calculadorPreferencias;

    @Override
    public PreferenciasDTORespuesta getReferencias(Integer idUsuario) {
        System.out.println("Obteniendo preferencias para el usuario con ID: " + idUsuario);

        List<CancionDTOEntrada> objCanciones = this.clienteCanciones.obtenerCancionesRemotas();
        System.out.println("Canciones obtenidas del servidor de canciones: " + objCanciones.size());

        List<ReproduccionesDTOEntrada> reproduccionesUsuario = this.clienteReproducciones.obtenerReproduccionesRemotas(idUsuario);
        System.out.println("Reproducciones obtenidas del servidor de reproducciones: " + reproduccionesUsuario.size());

        // Calcular las preferencias finales
        PreferenciasDTORespuesta resultado = this.calculadorPreferencias.calcular(idUsuario, objCanciones, reproduccionesUsuario);

        System.out.println("Preferencias calculadas exitosamente.");
        return resultado;
    }
}
