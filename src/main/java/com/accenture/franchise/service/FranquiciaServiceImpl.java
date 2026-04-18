package com.accenture.franchise.service;
import com.accenture.franchise.dto.TopProductoDTO;
import com.accenture.franchise.model.Franquicia;
import com.accenture.franchise.model.Producto;
import com.accenture.franchise.model.Sucursal;
import com.accenture.franchise.repository.FranquiciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FranquiciaServiceImpl implements FranquiciaService {

    private final FranquiciaRepository franquiciaRepository;

    // Crear franquicia
    @Override
    public Mono<Franquicia> crearFranquicia(String nombre) {
        Franquicia franquicia = Franquicia.builder()
                .id(UUID.randomUUID().toString())
                .nombre(nombre)
                .build();

        return franquiciaRepository.save(franquicia);
    }

    // Agregar sucursal
    @Override
    public Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(f -> {
                    Sucursal sucursal = Sucursal.builder()
                            .id(UUID.randomUUID().toString())
                            .nombre(nombreSucursal)
                            .build();

                    f.getSucursales().add(sucursal);
                    return franquiciaRepository.save(f);
                });
    }

    // Agregar producto
    @Override
    public Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, Integer stock) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(f -> {

                    Sucursal sucursal = f.getSucursales().stream()
                            .filter(s -> s.getId().equals(sucursalId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

                    Producto producto = Producto.builder()
                            .id(UUID.randomUUID().toString())
                            .nombre(nombreProducto)
                            .stock(stock)
                            .build();

                    sucursal.getProductos().add(producto);

                    return franquiciaRepository.save(f);
                });
    }

    // Eliminar producto
    @Override
    public Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(f -> {

                    f.getSucursales().forEach(s -> {
                        if (s.getId().equals(sucursalId)) {
                            s.setProductos(
                                    s.getProductos().stream()
                                            .filter(p -> !p.getId().equals(productoId))
                                            .toList()
                            );
                        }
                    });

                    return franquiciaRepository.save(f);
                });
    }

    // Actualizar stock
    @Override
    public Mono<Franquicia> actualizarStock(String franquiciaId, String productoId, Integer stock) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(f -> {

                    f.getSucursales().forEach(s ->
                            s.getProductos().forEach(p -> {
                                if (p.getId().equals(productoId)) {
                                    p.setStock(stock);
                                }
                            })
                    );

                    return franquiciaRepository.save(f);
                });
    }

    // Actualizar nombre franquicia
    @Override
    public Mono<Franquicia> actualizarNombreFranquicia(String franquiciaId, String nuevoNombre) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(f -> {
                    f.setNombre(nuevoNombre);
                    return franquiciaRepository.save(f);
                });
    }

    // Actualizar nombre sucursal
    @Override
    public Mono<Franquicia> actualizarNombreSucursal(String franquiciaId, String sucursalId, String nuevoNombre) {
        return franquiciaRepository.findById(franquiciaId)
                .flatMap(f -> {

                    f.getSucursales().stream()
                            .filter(s -> s.getId().equals(sucursalId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"))
                            .setNombre(nuevoNombre);

                    return franquiciaRepository.save(f);
                });
    }

    // Actualizar nombre producto
    @Override
    public Mono<Franquicia> actualizarNombreProducto(String franquiciaId, String productoId, String nuevoNombre) {
        return franquiciaRepository.findById(franquiciaId)
                .flatMap(f -> {

                    f.getSucursales().forEach(s ->
                            s.getProductos().stream()
                                    .filter(p -> p.getId().equals(productoId))
                                    .findFirst()
                                    .ifPresent(p -> p.setNombre(nuevoNombre))
                    );

                    return franquiciaRepository.save(f);
                });
    }

    // Obtener top-stock productos por sucursal
    @Override
    public Flux<TopProductoDTO> obtenerTopProductosPorSucursal(String franquiciaId) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMapMany(f -> Flux.fromIterable(f.getSucursales()))
                .flatMap(sucursal -> {
                    Producto top = sucursal.getProductos().stream()
                            .max(Comparator.comparingInt(Producto::getStock))
                            .orElse(null);

                    if (top == null) {
                        return Mono.empty();
                    }

                    return Mono.just(new TopProductoDTO(
                            sucursal.getNombre(),
                            top.getNombre(),
                            top.getStock()
                    ));
                });
    }
}