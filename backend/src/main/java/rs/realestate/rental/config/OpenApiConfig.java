package rs.realestate.rental.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracija Swagger/OpenAPI dokumentacije.
 * Definise Bearer JWT security scheme tako da se u Swagger UI-u
 * moze uneti token preko dugmeta "Authorize".
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearerAuth";

    @Bean
    public OpenAPI rentalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Real Estate Rental Platform API")
                        .description("REST API za aplikaciju za izdavanje nekretnina u zakup. "
                                + "Autentikacija preko JWT-a (Bearer token).")
                        .version("1.0.0")
                        .contact(new Contact().name("Nekretnine tim")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme()
                                .name(SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
