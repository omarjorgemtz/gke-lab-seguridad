package mx.com.gnp.seguridad.issues.infrastructure.config;

import mx.com.gnp.seguridad.issues.infrastructure.JpaEntidadRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = JpaEntidadRepository.class)
public class DatabaseConfig {
}
