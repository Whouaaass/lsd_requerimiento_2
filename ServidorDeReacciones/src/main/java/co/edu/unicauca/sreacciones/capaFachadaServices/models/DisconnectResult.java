package co.edu.unicauca.sreacciones.capaFachadaServices.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisconnectResult {

    private final String displayName;
    private final List<ChannelNotification> channelNotifications;
}
