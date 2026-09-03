package com.pragma.aws.apipersonas.infrastructure.adapter.in.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PersonaRouter {

    @Bean
    public RouterFunction<ServerResponse> route(PersonaHandler handler) {
        return RouterFunctions.route()
                .POST("/personas", handler::guardarPersona)
                .GET("/personas/{id}", handler::consultarPersona)
                .build();
    }
}
