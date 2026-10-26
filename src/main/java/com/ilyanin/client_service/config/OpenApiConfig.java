package com.ilyanin.client_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clientServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Client Service API")
                        .version("v1")
                        .description("""
                                CRUD-сервис учёта клиентов.
                                PUT выполняет полную замену изменяемых полей (fullName, gender, status).
                                Ошибки возвращаются в формате RFC 9457 (application/problem+json)."""))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local")));
    }
}
