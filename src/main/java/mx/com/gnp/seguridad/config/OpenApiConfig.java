package mx.com.gnp.seguridad.config;

import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    /**
     * Nombre de etiqueta para la autorización por JWT.
     */
    private static final String AUTH_KEY = "Authorization Bearer JWT";

    /**
     * Titulo/Nombre de la aplicación.
     */
    @Value("${application.title: Titulo del microservicio}")
    private String title;

    /**
     * Descripción de la aplicación.
     */
    @Value("${application.description: Descripción del microservicio}")
    private String description;

    /**
     * Versión de la aplicación.
     */
    @Value("${application.version:1.0.0}")
    private String version;

    /**
     * @return Bean de {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(AUTH_KEY))
                .components(getComponents())
                .info(getInfo());
    }

    private Components getComponents() {
        return new Components()
                .addSecuritySchemes(AUTH_KEY,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }

    /**
     * INFO.
     * @return info
     */
    private Info getInfo() {
        return new Info()
                .title(title)
                .version(version)
                .description(description);
    }

}
