package com.talecraft.talecraftbe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("JwtToken");

        // Security Requirement 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("CookieAuth");

        return new OpenAPI()
                .info(new Info().title("TaleCraft API")
                        .description("TaleCraft Application API Documentation")
                        .version("v1.0"))
                .addSecurityItem(securityRequirement)
                .schemaRequirement("CookieAuth", securityScheme);
    }
}
