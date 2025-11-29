package co.edu.unicauca.sreacciones.capaFachadaServices.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelNotification {

    private final String destination;
    private final String message;
}
