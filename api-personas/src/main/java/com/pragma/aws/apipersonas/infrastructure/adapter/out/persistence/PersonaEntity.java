package com.pragma.aws.apipersonas.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("persona")
public record PersonaEntity(
    @Id
    Long id,
    String nombre,
    String email
) {
}
