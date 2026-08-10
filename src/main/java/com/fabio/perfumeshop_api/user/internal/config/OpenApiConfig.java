package com.fabio.perfumeshop_api.user.internal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura Swagger UI para que sepa que la API se protege con un token Bearer (JWT).
 * Sin esto no aparece el botón "Authorize" (el candado) y habría que probar con curl.
 */
@Configuration
class OpenApiConfig {

    // Nombre interno del esquema de seguridad; lo referencian el requirement y el scheme.
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    OpenAPI perfumeShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PerfumeShop API")
                        .version("v1")
                        .description("API REST de una tienda de fragancias."))
                // Aplica el esquema a toda la API por defecto (candado global en Swagger UI).
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP) // HTTP auth...
                                        .scheme("bearer")                // ...esquema Bearer...
                                        .bearerFormat("JWT")));          // ...con formato JWT.
    }
}
