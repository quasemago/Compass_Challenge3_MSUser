package com.compassuol.sp.challenge.msuser.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Token Authentication")
                )
                .components(new Components()
                        .addSecuritySchemes("Bearer Token Authentication", createSecurityScheme())
                )
                .info(new Info()
                        .title("CompassUOL - Challenge 3 - MS User API")
                        .description("API em microserviço para gerenciamento de usuários")
                        .version("v1")
                        .contact(new Contact()
                                .name("Bruno \"quasemago\" Ronning")
                                .email("brunoronningfn@gmail.com")
                                .url("https://github.com/quasemago"))
                );
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("Bearer Token Authentication")
                .description("Insira um Bearer Token para autenticação")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
