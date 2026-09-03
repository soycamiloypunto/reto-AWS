package com.pragma.aws.apipersonas.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SpringDataPersonaRepository extends ReactiveCrudRepository<PersonaEntity, Long> {
}
