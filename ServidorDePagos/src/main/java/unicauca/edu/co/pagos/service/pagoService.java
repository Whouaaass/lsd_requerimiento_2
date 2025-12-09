package unicauca.edu.co.pagos.service;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import unicauca.edu.co.pagos.dtos.pagoPeticionDTO;
import unicauca.edu.co.pagos.model.pago;

@Service
public class pagoService {

    private static final Logger log = LoggerFactory.getLogger(pagoService.class);

    // token -> usado?
    private final Map<String, Boolean> tokens = new HashMap<>();

    // usuario -> pagos
    private final Map<String, List<pago>> pagosPorUsuario = new HashMap<>();

    // usuario -> total acumulado
    private final Map<String, Integer> totalPorUsuario = new HashMap<>();

    private final Random random = new Random();

    // --------- TOKENS ---------

    public String generarToken(String usuario) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, Boolean.FALSE);
        log.info("Token generado para {}: {}", usuario, token);
        return token;
    }

    public boolean tokenExiste(String token) {
        return tokens.containsKey(token);
    }

    public boolean tokenUsado(String token) {
        return tokens.getOrDefault(token, true);
    }

    public void marcarTokenUsado(String token) {
        tokens.put(token, Boolean.TRUE);
    }

    // --------- PAGOS ---------

    /**
     * Intenta registrar un pago.
     * @return true si el pago fue exitoso, false si se simula un fallo.
     */
    public boolean registrarPago(pagoPeticionDTO dto) {

        // ---- 1. Validar token ----
        if (!tokenExiste(dto.getToken())) {
            log.warn("Pago rechazado. Token inexistente: {}", dto.getToken());
            throw new IllegalArgumentException("Token inexistente");
        }

        if (tokenUsado(dto.getToken())) {
            log.warn("Pago rechazado. Token ya usado: {}", dto.getToken());
            throw new IllegalStateException("Token ya usado");
        }

        // ---- 2. Simular fallo de pago (por ejemplo 30% de probabilidad) ----
        boolean fallo = random.nextDouble() < 0.3;
        if (fallo) {
            log.warn("Simulando fallo de pago para usuario {}", dto.getUsuario());
            return false; // el caller (micro de reacciones) decidirá reintentar
        }

        // ---- 3. Registrar pago como exitoso ----
        marcarTokenUsado(dto.getToken());

        pago pago = new pago(
                dto.getUsuario(),
                dto.getCancionId(),
                dto.getValor(),
                dto.getToken()
        );

        pagosPorUsuario
                .computeIfAbsent(dto.getUsuario(), u -> new ArrayList<>())
                .add(pago);

        totalPorUsuario.merge(dto.getUsuario(), dto.getValor(), Integer::sum);

        // Eco del pago
        log.info("PAGO EXITOSO => usuario: {}, cancion: {}, valor: {}, token: {}",
                pago.getUsuario(), pago.getCancionId(), pago.getValor(), pago.getToken());

        return true;
    }

    public int obtenerTotalUsuario(String usuario) {
        return totalPorUsuario.getOrDefault(usuario, 0);
    }

    public List<pago> obtenerPagosUsuario(String usuario) {
        return pagosPorUsuario.getOrDefault(usuario, Collections.emptyList());
    }
}


