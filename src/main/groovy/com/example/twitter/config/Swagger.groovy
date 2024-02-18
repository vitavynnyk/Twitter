package com.example.twitter.config

import io.swagger.v3.oas.models.OpenAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server

@Configuration
class Swagger {
    @Bean
    OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title("Application API")
                .version("1.0")
                .description("Twitter Application")
                .license(new License().name("Apache 2.0")
                        .url("http://springdoc.org")))
                .servers(List.of(new Server().url("http://localhost:8080")))

    }
}