package com.github.cidarosa.ms.pedidos.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;

import java.util.List;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microsserviço de Pedidos")
                        .description("API responsável pelo gerenciamento de pedidos")
                        .version("v1"))

                .servers(List.of(
                        new Server().url("/ms-pedidos")
                ));
    }
}
