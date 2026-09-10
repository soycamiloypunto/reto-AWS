package com.pragma.aws.apipersonas.infrastructure.adapter.in.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import com.pragma.aws.apipersonas.domain.model.Persona;

@Configuration
public class PersonaRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(path = "/personas", produces = { MediaType.APPLICATION_JSON_VALUE }, 
            method = RequestMethod.POST, beanClass = PersonaHandler.class, beanMethod = "guardarPersona",
            operation = @Operation(operationId = "guardarPersona", summary = "Crear nueva persona", 
            description = "Almacena una persona en la base de datos",
            requestBody = @RequestBody(required = true, description = "Datos de la persona", 
                content = @Content(schema = @Schema(implementation = Persona.class))),
            responses = {
                @ApiResponse(responseCode = "201", description = "Persona creada exitosamente", 
                    content = @Content(schema = @Schema(implementation = Persona.class))),
                @ApiResponse(responseCode = "400", description = "Error de validación")
            })),
        @RouterOperation(path = "/personas/{id}", produces = { MediaType.APPLICATION_JSON_VALUE }, 
            method = RequestMethod.GET, beanClass = PersonaHandler.class, beanMethod = "consultarPersona",
            operation = @Operation(operationId = "consultarPersona", summary = "Obtener una persona", 
            description = "Consulta a una persona por su identificador único",
            parameters = { @Parameter(in = ParameterIn.PATH, name = "id", description = "ID de la persona") },
            responses = {
                @ApiResponse(responseCode = "200", description = "Persona encontrada", 
                    content = @Content(schema = @Schema(implementation = Persona.class))),
                @ApiResponse(responseCode = "404", description = "Persona no encontrada")
            }))
    })
    public RouterFunction<ServerResponse> route(PersonaHandler handler) {
        return RouterFunctions.route()
                .POST("/personas", handler::guardarPersona)
                .GET("/personas/{id}", handler::consultarPersona)
                .build();
    }
}
