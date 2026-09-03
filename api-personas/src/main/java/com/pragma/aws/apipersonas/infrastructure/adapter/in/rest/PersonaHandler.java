package com.pragma.aws.apipersonas.infrastructure.adapter.in.rest;

import com.pragma.aws.apipersonas.application.port.in.IPersonaUseCase;
import com.pragma.aws.apipersonas.domain.model.Persona;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class PersonaHandler {

    private final IPersonaUseCase personaUseCase;

    public PersonaHandler(IPersonaUseCase personaUseCase) {
        this.personaUseCase = personaUseCase;
    }

    public Mono<ServerResponse> guardarPersona(ServerRequest request) {
        return request.bodyToMono(Persona.class)
                .flatMap(personaUseCase::guardarPersona)
                .flatMap(personaGuardada -> ServerResponse
                        .created(URI.create("/personas/" + personaGuardada.id()))
                        .bodyValue(personaGuardada))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> consultarPersona(ServerRequest request) {
        return Mono.just(request.pathVariable("id"))
                .map(Long::valueOf)
                .flatMap(personaUseCase::consultarPersona)
                .flatMap(persona -> ServerResponse.ok().bodyValue(persona))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(e.getMessage()));
    }
}
