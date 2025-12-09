package co.edu.unicauca.sreacciones.capaFachadaServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sreacciones.capaFachadaServices.DTO.MessageCancionDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.PagosClientImpl;
import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto.PeticionPagoDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto.PeticionPagoResponse;
import co.edu.unicauca.sreacciones.capaFachadaServices.services.ReenvioService;
import feign.FeignException;

@Service
public class CancionesServiceImpl implements ICancionesService {

    private static final Logger logger = LoggerFactory.getLogger(CancionesServiceImpl.class);

    @Autowired
    private ReenvioService reenvioService;

    @Autowired
    private PagosClientImpl pagosClient;

    private static final int COSTO_REACCION = 10;

    @Override
    public void procesarMensaje(MessageCancionDTO message, String sessionId) {
        if (message.getType().equals("reaction")) {
            boolean success = this.procesarReaccion(
                    message.getUserNickname(),
                    message.getIdCancion(),
                    message.getContent(),
                    sessionId
            );
            if (!success) {
                return;
            }
        }
        reenvioService.reenviarMensajesACancion(message);
    }

    private boolean procesarReaccion(String usuario, Integer cancionId, String tipo, String sessionId) {
        System.out.println("Usuario " + usuario + " quiere reaccionar (" + tipo + ") a canción " + cancionId);

        String token;

        try {
            // 1. Pedir token al micro de pagos
            token = pagosClient.getToken(usuario);

            Integer total = pagosClient.obtenerTotal(usuario);
            if (total != null && total >= 50) {
                logger.info("El usuario {} ha superado más de $50, no se permite reaccionar", usuario);
                reenvioService.enviarMensajeUsuario(usuario, "alert", "Se ha alcanzado el límite de $50");
                return false;
            }

            // 2. Enviar el pago
            PeticionPagoDTO pagoReq = new PeticionPagoDTO(
                    token,
                    usuario,
                    cancionId.toString(),
                    COSTO_REACCION
            );

            logger.info("Enviando pago para " + usuario);
            PeticionPagoResponse pagoOK = pagosClient.enviarPago(pagoReq);
            

            if (!pagoOK.getExito()) {
                logger.error("Pago NO OK, se encola reacción para reintento: " + usuario);
                
                return false;
            }

        } catch (FeignException ex) {   
            logger.error("Error inesperado llamando a pagos, se encola reacción: " + ex.getMessage());
            return false;

        } catch (Exception ex) {
            return false;
        }

        // 3. Si el pago fue exitoso, actualizamos totales y enviamos la reacción
        //totalesUsuarioService.sumar(usuario, COSTO_REACCION);
        //int nuevoTotal = totalesUsuarioService.obtenerTotal(usuario);
        //logger.info("Nuevo total para " + usuario + ": " + nuevoTotal);
        return true;
    }

    

}
