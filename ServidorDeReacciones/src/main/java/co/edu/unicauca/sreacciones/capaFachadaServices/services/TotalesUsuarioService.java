package co.edu.unicauca.sreacciones.capaFachadaServices.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
/**  
 * @deprecated Esto debe pasarse a el servidor de pagos
 */
public class TotalesUsuarioService {

    private final Map<String, Integer> totalPorUsuario = new HashMap<>();

    public int obtenerTotal(String usuario) {
        return totalPorUsuario.getOrDefault(usuario, 0);
    }

    public void sumar(String usuario, int valor) {
        int nuevo = obtenerTotal(usuario) + valor;
        totalPorUsuario.put(usuario, nuevo);
    }
}
