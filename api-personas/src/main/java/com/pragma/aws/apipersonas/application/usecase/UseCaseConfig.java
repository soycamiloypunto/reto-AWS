package com.pragma.aws.apipersonas.application.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pragma.aws.apipersonas.application.port.out.IPersonaRepository;
import com.pragma.aws.apipersonas.application.port.in.IPersonaUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public IPersonaUseCase personaUseCase(IPersonaRepository personaRepository) {
        return new PersonaUseCase(personaRepository);
    }
}
