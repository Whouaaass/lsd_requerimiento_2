package co.edu.unicauca.sreacciones.capaAccesoADatos.models;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSession {

    private String nickname;
    private Map<String, String> subscripciones;

    public void addSubscripcion(String subscriptionId, String destination) {
        subscripciones.put(subscriptionId, destination);
    }

    public String removeSubscripcion(String subscriptionId) {
        return subscripciones.remove(subscriptionId);
    }
}
