package com.accenture.franchise.controller;
import com.accenture.franchise.dto.*;
import com.accenture.franchise.model.Franquicia;
import com.accenture.franchise.service.FranquiciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franquicias")
@RequiredArgsConstructor
public class FranquiciaController {

    private final FranquiciaService franquiciaService;

    // ✅ Crear franquicia
    @PostMapping
    public Mono<ResponseEntity<Franquicia>> crearFranquicia(
            @RequestBody @Valid CrearFranquiciaRequest request) {

        return franquiciaService.crearFranquicia(request.getNombre())
                .map(ResponseEntity::ok);
    }

    // ✅ Agregar sucursal
    @PostMapping("/{franquiciaId}/sucursales")
    public Mono<ResponseEntity<Franquicia>> agregarSucursal(
            @PathVariable String franquiciaId,
            @RequestBody @Valid CrearSucursalRequest request) {

        return franquiciaService.agregarSucursal(franquiciaId, request.getNombre())
                .map(ResponseEntity::ok);
    }

    // ✅ Agregar producto
    @PostMapping("/{franquiciaId}/sucursales/{sucursalId}/productos")
    public Mono<ResponseEntity<Franquicia>> agregarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @RequestBody @Valid CrearProductoRequest request) {

        return franquiciaService.agregarProducto(
                        franquiciaId,
                        sucursalId,
                        request.getNombre(),
                        request.getStock()
                )
                .map(ResponseEntity::ok);
    }

    // ✅ Eliminar producto
    @DeleteMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    public Mono<ResponseEntity<Franquicia>> eliminarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId) {

        return franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId)
                .map(ResponseEntity::ok);
    }

    // ✅ Actualizar stock
    @PatchMapping("/{franquiciaId}/productos/{productoId}/stock")
    public Mono<ResponseEntity<Franquicia>> actualizarStock(
            @PathVariable String franquiciaId,
            @PathVariable String productoId,
            @RequestBody @Valid ActualizarStockRequest request) {

        return franquiciaService.actualizarStock(
                        franquiciaId,
                        productoId,
                        request.getStock()
                )
                .map(ResponseEntity::ok);
    }

    // ✅ Actualizar nombre franquicia
    @PatchMapping("/{franquiciaId}/nombre")
    public Mono<ResponseEntity<Franquicia>> actualizarNombreFranquicia(
            @PathVariable String franquiciaId,
            @RequestBody @Valid ActualizarNombreRequest request) {

        return franquiciaService
                .actualizarNombreFranquicia(franquiciaId, request.getNombre())
                .map(ResponseEntity::ok);
    }

    // ✅ Actualizar nombre sucursal
    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/nombre")
    public Mono<ResponseEntity<Franquicia>> actualizarNombreSucursal(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @RequestBody @Valid ActualizarNombreRequest request) {

        return franquiciaService
                .actualizarNombreSucursal(franquiciaId, sucursalId, request.getNombre())
                .map(ResponseEntity::ok);
    }

    // ✅ Actualizar nombre producto
    @PatchMapping("/{franquiciaId}/productos/{productoId}/nombre")
    public Mono<ResponseEntity<Franquicia>> actualizarNombreProducto(
            @PathVariable String franquiciaId,
            @PathVariable String productoId,
            @RequestBody @Valid ActualizarNombreRequest request) {

        return franquiciaService
                .actualizarNombreProducto(franquiciaId, productoId, request.getNombre())
                .map(ResponseEntity::ok);
    }

    // 🔥 Endpoint clave
    @GetMapping("/{franquiciaId}/top-productos")
    public Flux<TopProductoDTO> obtenerTopProductos(@PathVariable String franquiciaId) {
        return franquiciaService.obtenerTopProductosPorSucursal(franquiciaId);
    }
}