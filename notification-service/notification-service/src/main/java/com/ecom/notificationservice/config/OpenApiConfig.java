package com.ecom.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .description("Microservice for managing notifications in the E-Commerce platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("E-Commerce Team")
                                .email("support@ecommerce.com")
                                .url("https://www.ecommerce.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8087")
                                .description("Notification Service Server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("API Gateway Server")
                ));
    }
}
