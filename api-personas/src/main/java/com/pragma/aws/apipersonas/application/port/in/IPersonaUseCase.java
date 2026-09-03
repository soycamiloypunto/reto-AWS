package com.pragma.aws.apipersonas.application.port.in;

import com.pragma.aws.apipersonas.domain.model.Persona;
import reactor.core.publisher.Mono;

public interface IPersonaUseCase {
    Mono<Persona> guardarPersona(Persona persona);
    Mono<Persona> consultarPersona(Long id);
}
