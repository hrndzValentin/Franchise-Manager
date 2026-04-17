package com.accenture.franchise.service;

import com.accenture.franchise.dto.TopProductoDTO;
import com.accenture.franchise.model.Franquicia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranquiciaService {

    Mono<Franquicia> crearFranquicia(String nombre);

    Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal);

    Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, Integer stock);

    Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId);

    Mono<Franquicia> actualizarStock(String franquiciaId, String productoId, Integer stock);

    Mono<Franquicia> actualizarNombreFranquicia(String franquiciaId, String nuevoNombre);

    Mono<Franquicia> actualizarNombreSucursal(String franquiciaId, String sucursalId, String nuevoNombre);

    Mono<Franquicia> actualizarNombreProducto(String franquiciaId, String productoId, String nuevoNombre);

    Flux<TopProductoDTO> obtenerTopProductosPorSucursal(String franquiciaId);
}