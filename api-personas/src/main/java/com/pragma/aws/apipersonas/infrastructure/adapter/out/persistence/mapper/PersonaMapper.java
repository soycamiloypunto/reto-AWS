package com.pragma.aws.apipersonas.infrastructure.adapter.out.persistence.mapper;

import com.pragma.aws.apipersonas.domain.model.Persona;
import com.pragma.aws.apipersonas.infrastructure.adapter.out.persistence.PersonaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonaMapper {
    PersonaEntity toEntity(Persona persona);
    Persona toDomain(PersonaEntity entity);
}
