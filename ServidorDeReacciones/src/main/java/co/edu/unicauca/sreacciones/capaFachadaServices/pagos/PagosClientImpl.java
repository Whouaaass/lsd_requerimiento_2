package co.edu.unicauca.sreacciones.capaFachadaServices.pagos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto.PeticionPagoDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto.PeticionPagoResponse;
import feign.FeignException;

@Service
public class PagosClientImpl {
    
    @Autowired
    private IPagosClient pagosClient;

    @Retryable(value = {FeignException.class}, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 2000))
    public String getToken(String usuario) {
        System.out.println("Solicitando token");
        return pagosClient.solicitarToken(usuario).getToken();
    }

    @Retryable(value = {FeignException.class}, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 2000))
    public PeticionPagoResponse enviarPago(PeticionPagoDTO dto) {
        System.out.println("Enviando pago..");
        return pagosClient.enviarPago(dto);
    }

    @Retryable(value = {FeignException.class}, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 2000))
    public Integer obtenerTotal(String usuario) {
        System.out.println("Obteniendo total..");
        return pagosClient.obtenerTotal(usuario);        
    }


    @Recover
    public String handleRecover(FeignException ex, String usuario) {
        return "Error al solicitar el token";
    }

    @Recover
    public String handleRecover(FeignException ex, PeticionPagoDTO dto) {
        return "Error al enviar el pago";
    }

}
