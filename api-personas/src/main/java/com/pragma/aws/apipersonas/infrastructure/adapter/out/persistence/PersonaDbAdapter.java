package com.pragma.aws.apipersonas.infrastructure.adapter.out.persistence;

import com.pragma.aws.apipersonas.application.port.out.IPersonaRepository;
import com.pragma.aws.apipersonas.domain.model.Persona;
import com.pragma.aws.apipersonas.infrastructure.adapter.out.persistence.mapper.PersonaMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class PersonaDbAdapter implements IPersonaRepository {

    private final SpringDataPersonaRepository repository;
    private final PersonaMapper personaMapper;

    public PersonaDbAdapter(SpringDataPersonaRepository repository, PersonaMapper personaMapper) {
        this.repository = repository;
        this.personaMapper = personaMapper;
    }

    @Override
    public Mono<Persona> save(Persona persona) {
        return Mono.just(persona)
                .map(personaMapper::toEntity)
                .flatMap(repository::save)
                .map(personaMapper::toDomain);
    }

    @Override
    public Mono<Persona> findById(Long id) {
        return repository.findById(id)
                .map(personaMapper::toDomain);
    }
}
