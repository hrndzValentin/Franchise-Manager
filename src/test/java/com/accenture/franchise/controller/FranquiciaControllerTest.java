package com.accenture.franchise.controller;

import com.accenture.franchise.dto.CrearFranquiciaRequest;
import com.accenture.franchise.dto.CrearSucursalRequest;
import com.accenture.franchise.dto.TopProductoDTO;
import com.accenture.franchise.model.Franquicia;
import com.accenture.franchise.service.FranquiciaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(FranquiciaController.class)
class FranquiciaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranquiciaService franquiciaService;

    @Test
    void crearFranquicia_ShouldReturnOk() {
        CrearFranquiciaRequest request = new CrearFranquiciaRequest();
        request.setNombre("Franquicia 1");

        Franquicia franquicia = Franquicia.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Franquicia 1")
                .build();

        when(franquiciaService.crearFranquicia(anyString())).thenReturn(Mono.just(franquicia));

        webTestClient.post()
                .uri("/api/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Franquicia 1")
                .jsonPath("$.id").exists();
    }

    @Test
    void agregarSucursal_ShouldReturnOk() {
        CrearSucursalRequest request = new CrearSucursalRequest();
        request.setNombre("Sucursal 1");

        Franquicia franquicia = Franquicia.builder()
                .id("f-1")
                .nombre("Franquicia 1")
                .build();

        when(franquiciaService.agregarSucursal(anyString(), anyString())).thenReturn(Mono.just(franquicia));

        webTestClient.post()
                .uri("/api/franquicias/f-1/sucursales")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Franquicia 1");
    }

    @Test
    void obtenerTopProductos_ShouldReturnFlux() {
        TopProductoDTO dto = new TopProductoDTO("Suc1", "Prod1", 100);

        when(franquiciaService.obtenerTopProductosPorSucursal("f-1")).thenReturn(Flux.just(dto));

        webTestClient.get()
                .uri("/api/franquicias/f-1/top-productos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TopProductoDTO.class)
                .hasSize(1)
                .contains(dto);
    }

    @Test
    void crearFranquicia_WhenServiceThrowsError_ShouldReturn500() {
        CrearFranquiciaRequest request = new CrearFranquiciaRequest();
        request.setNombre("Error Franchise");

        when(franquiciaService.crearFranquicia(anyString())).thenReturn(Mono.error(new RuntimeException("DB Error")));

        webTestClient.post()
                .uri("/api/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unexpected error");
    }
}
