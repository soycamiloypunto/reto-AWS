package com.pragma.aws.apipersonas.infrastructure.adapter.in.rest;

import com.pragma.aws.apipersonas.application.port.in.IPersonaUseCase;
import com.pragma.aws.apipersonas.domain.model.Persona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import({PersonaRouter.class, PersonaHandler.class})
class PersonaHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private IPersonaUseCase personaUseCase;

    @Test
    @DisplayName("Dada una peticion POST valida, cuando llega al handler, entonces retorna 201 Created y el ID")
    void dadaUnaPeticionPostValida_cuandoLlegaAlHandler_entoncesRetorna201CreatedYElId() {
        // Arrange
        Persona personaPeticion = new Persona(null, "Pedro", "pedro@test.com");
        Persona personaGuardada = new Persona(2L, "Pedro", "pedro@test.com");
        when(personaUseCase.guardarPersona(any(Persona.class))).thenReturn(Mono.just(personaGuardada));

        // Act & Assert
        webTestClient.post()
                .uri("/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(personaPeticion)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/personas/2")
                .expectBody()
                .jsonPath("$.id").isEqualTo(2)
                .jsonPath("$.nombre").isEqualTo("Pedro");
    }

    @Test
    @DisplayName("Dado un GET a un ID existente, cuando llega al handler, entonces retorna 200 OK y la persona")
    void dadoUnGetAUnIdExistente_cuandoLlegaAlHandler_entoncesRetorna200OkYLaPersona() {
        // Arrange
        Long id = 1L;
        Persona persona = new Persona(1L, "Ana", "ana@test.com");
        when(personaUseCase.consultarPersona(id)).thenReturn(Mono.just(persona));

        // Act & Assert
        webTestClient.get()
                .uri("/personas/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombre").isEqualTo("Ana");
    }

    @Test
    @DisplayName("Dado un GET a un ID inexistente, cuando llega al handler, entonces retorna 404 Not Found")
    void dadoUnGetAUnIdInexistente_cuandoLlegaAlHandler_entoncesRetorna404NotFound() {
        // Arrange
        Long id = 99L;
        when(personaUseCase.consultarPersona(id)).thenReturn(Mono.error(new RuntimeException("Persona no encontrada")));

        // Act & Assert
        webTestClient.get()
                .uri("/personas/99")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("Persona no encontrada");
    }
}
