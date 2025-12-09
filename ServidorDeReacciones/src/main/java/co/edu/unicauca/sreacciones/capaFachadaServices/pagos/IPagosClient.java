package co.edu.unicauca.sreacciones.capaFachadaServices.pagos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto.PeticionPagoDTO;
import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto.PeticionPagoResponse;
import co.edu.unicauca.sreacciones.capaFachadaServices.pagos.dto.PeticionTokenResponse;

@FeignClient(
        name = "pagosClient",
        url = "${servidor.pagos.url}/api/pagos",
        configuration = FeignConfig.class
)
public interface IPagosClient {

    /**
     * Genera un token para pagar con un usuario
     * @param usuario
     * @return
     */
    @GetMapping("/token")
    PeticionTokenResponse solicitarToken(@RequestParam String usuario);

    /**
     * Realiza un pago
     * @param dto
     * @return
     */
    @PostMapping("/pago")
    PeticionPagoResponse enviarPago(@RequestBody PeticionPagoDTO dto);

    /**
     * Obtiene el total de un usuario
     * @param usuario
     * @return
     */
    @GetMapping("/total")
    Integer obtenerTotal(@RequestParam() String usuario);

}
