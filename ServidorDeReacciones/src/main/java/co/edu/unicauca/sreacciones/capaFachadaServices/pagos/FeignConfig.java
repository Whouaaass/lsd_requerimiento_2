package co.edu.unicauca.sreacciones.capaFachadaServices.pagos;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options getOptions() {
        return new Request.Options(
                Duration.ofSeconds(3), // Connection timeout
                Duration.ofSeconds(3), // Read timeout
                false // Allow redirects
        );
    }

}
