package co.edu.unicauca.lsd.servidor_reproducciones.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("co.edu.unicauca.lsd.servidor_reproducciones.domain")
@EnableJpaRepositories("co.edu.unicauca.lsd.servidor_reproducciones.repos")
@EnableTransactionManagement
public class DomainConfig {
}
