package com.pragma.aws.apipersonas.application.usecase;

import com.pragma.aws.apipersonas.application.port.in.IPersonaUseCase;
import com.pragma.aws.apipersonas.application.port.out.IPersonaRepository;
import com.pragma.aws.apipersonas.domain.model.Persona;
import reactor.core.publisher.Mono;

public class PersonaUseCase implements IPersonaUseCase {

    private final IPersonaRepository personaRepository;

    public PersonaUseCase(IPersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public Mono<Persona> guardarPersona(Persona persona) {
        return Mono.just(persona)
                .flatMap(personaRepository::save);
    }

    @Override
    public Mono<Persona> consultarPersona(Long id) {
        return Mono.just(id)
                .flatMap(personaRepository::findById)
                .switchIfEmpty(Mono.error(new RuntimeException("Persona no encontrada con ID: " + id)));
    }
}
