package com.pragma.aws.apipersonas.application.port.out;

import com.pragma.aws.apipersonas.domain.model.Persona;
import reactor.core.publisher.Mono;

public interface IPersonaRepository {
    Mono<Persona> save(Persona persona);
    Mono<Persona> findById(Long id);
}
