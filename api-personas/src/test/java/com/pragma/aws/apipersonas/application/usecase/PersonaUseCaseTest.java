package com.pragma.aws.apipersonas.application.usecase;

import com.pragma.aws.apipersonas.application.port.out.IPersonaRepository;
import com.pragma.aws.apipersonas.domain.model.Persona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonaUseCaseTest {

    @Mock
    private IPersonaRepository personaRepository;

    @InjectMocks
    private PersonaUseCase personaUseCase;

    @Test
    @DisplayName("Dado una persona valida, cuando se solicita guardar, entonces retorna la persona con ID")
    void dadaUnaPersonaValida_cuandoSeSolicitaGuardar_entoncesRetornaPersonaConId() {
        // Arrange
        Persona personaPeticion = new Persona(null, "Juan", "juan@test.com");
        Persona personaGuardada = new Persona(1L, "Juan", "juan@test.com");

        when(personaRepository.save(any(Persona.class))).thenReturn(Mono.just(personaGuardada));

        // Act
        Mono<Persona> resultado = personaUseCase.guardarPersona(personaPeticion);

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(persona -> 
                    persona.id().equals(1L) && 
                    persona.nombre().equals("Juan") &&
                    persona.email().equals("juan@test.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Dado un ID existente, cuando se consulta, entonces retorna los datos de la persona")
    void dadoUnIdExistente_cuandoSeConsulta_entoncesRetornaDatosDePersona() {
        // Arrange
        Long idConsulta = 1L;
        Persona personaEncontrada = new Persona(1L, "Maria", "maria@test.com");

        when(personaRepository.findById(idConsulta)).thenReturn(Mono.just(personaEncontrada));

        // Act
        Mono<Persona> resultado = personaUseCase.consultarPersona(idConsulta);

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(persona -> 
                    persona.id().equals(1L) && 
                    persona.nombre().equals("Maria"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Dado un ID inexistente, cuando se consulta, entonces retorna un error")
    void dadoUnIdInexistente_cuandoSeConsulta_entoncesRetornaError() {
        // Arrange
        Long idInexistente = 99L;

        when(personaRepository.findById(idInexistente)).thenReturn(Mono.empty());

        // Act
        Mono<Persona> resultado = personaUseCase.consultarPersona(idInexistente);

        // Assert
        StepVerifier.create(resultado)
                .expectErrorMessage("Persona no encontrada con ID: 99")
                .verify();
    }
}
