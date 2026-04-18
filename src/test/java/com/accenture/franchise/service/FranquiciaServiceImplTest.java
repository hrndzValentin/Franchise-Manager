package com.accenture.franchise.service;

import com.accenture.franchise.dto.TopProductoDTO;
import com.accenture.franchise.model.Franquicia;
import com.accenture.franchise.model.Producto;
import com.accenture.franchise.model.Sucursal;
import com.accenture.franchise.repository.FranquiciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceImplTest {

    @Mock
    private FranquiciaRepository franquiciaRepository;

    @InjectMocks
    private FranquiciaServiceImpl franquiciaService;

    private Franquicia baseFranquicia;

    @BeforeEach
    void setUp() {
        baseFranquicia = Franquicia.builder()
                .id("f-123")
                .nombre("Test Franchise")
                .sucursales(new ArrayList<>())
                .build();
    }

    @Test
    void crearFranquicia_ShouldReturnSavedFranquicia() {
        String nombre = "Nueva Franquicia";
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.crearFranquicia(nombre))
                .expectNextMatches(f -> f.getNombre().equals(nombre) && f.getId() != null)
                .verifyComplete();

        verify(franquiciaRepository).save(any(Franquicia.class));
    }

    @Test
    void agregarSucursal_ShouldAddSucursalToFranquicia() {
        String franquiciaId = "f-123";
        String nombreSucursal = "Nueva Sucursal";
        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.agregarSucursal(franquiciaId, nombreSucursal))
                .expectNextMatches(f -> f.getSucursales().size() == 1 && 
                                       f.getSucursales().get(0).getNombre().equals(nombreSucursal))
                .verifyComplete();

        verify(franquiciaRepository).findById(franquiciaId);
        verify(franquiciaRepository).save(any(Franquicia.class));
    }

    @Test
    void agregarSucursal_WhenFranquiciaNotFound_ShouldReturnError() {
        String franquiciaId = "not-found";
        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.empty());

        StepVerifier.create(franquiciaService.agregarSucursal(franquiciaId, "Sucursal"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Franquicia no encontrada"))
                .verify();

        verify(franquiciaRepository).findById(franquiciaId);
        verify(franquiciaRepository, never()).save(any());
    }

    @Test
    void agregarProducto_ShouldAddProductoToSucursal() {
        String franquiciaId = "f-123";
        String sucursalId = "s-456";
        String nombreProducto = "Nuevo Producto";
        Integer stock = 100;

        Sucursal sucursal = Sucursal.builder().id(sucursalId).nombre("Sucursal 1").productos(new ArrayList<>()).build();
        baseFranquicia.getSucursales().add(sucursal);

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.agregarProducto(franquiciaId, sucursalId, nombreProducto, stock))
                .expectNextMatches(f -> {
                    Sucursal s = f.getSucursales().get(0);
                    return s.getProductos().size() == 1 &&
                           s.getProductos().get(0).getNombre().equals(nombreProducto) &&
                           s.getProductos().get(0).getStock().equals(stock);
                })
                .verifyComplete();

        verify(franquiciaRepository).findById(franquiciaId);
        verify(franquiciaRepository).save(any(Franquicia.class));
    }

    @Test
    void agregarProducto_WhenSucursalNotFound_ShouldReturnError() {
        String franquiciaId = "f-123";
        String sucursalId = "not-found";
        
        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));

        StepVerifier.create(franquiciaService.agregarProducto(franquiciaId, sucursalId, "Prod", 10))
                .expectErrorMatches(t -> t instanceof RuntimeException && t.getMessage().equals("Sucursal no encontrada"))
                .verify();

        verify(franquiciaRepository, never()).save(any());
    }

    @Test
    void eliminarProducto_ShouldRemoveProductoFromSucursal() {
        String franquiciaId = "f-123";
        String sucursalId = "s-456";
        String productoId = "p-789";

        Producto producto = Producto.builder().id(productoId).nombre("Prod").stock(10).build();
        Sucursal sucursal = Sucursal.builder().id(sucursalId).nombre("Sucursal").productos(new ArrayList<>(List.of(producto))).build();
        baseFranquicia.setSucursales(new ArrayList<>(List.of(sucursal)));

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId))
                .expectNextMatches(f -> f.getSucursales().get(0).getProductos().isEmpty())
                .verifyComplete();

        verify(franquiciaRepository).save(any(Franquicia.class));
    }

    @Test
    void actualizarStock_ShouldUpdateProductoStock() {
        String franquiciaId = "f-123";
        String productoId = "p-789";
        Integer nuevoStock = 500;

        Producto producto = Producto.builder().id(productoId).nombre("Prod").stock(10).build();
        Sucursal sucursal = Sucursal.builder().id("s-1").nombre("Sucursal").productos(new ArrayList<>(List.of(producto))).build();
        baseFranquicia.setSucursales(new ArrayList<>(List.of(sucursal)));

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.actualizarStock(franquiciaId, productoId, nuevoStock))
                .expectNextMatches(f -> f.getSucursales().get(0).getProductos().get(0).getStock().equals(nuevoStock))
                .verifyComplete();
    }

    @Test
    void obtenerTopProductosPorSucursal_ShouldReturnTopProducts() {
        String franquiciaId = "f-123";

        Producto p1 = Producto.builder().id("p1").nombre("P1").stock(10).build();
        Producto p2 = Producto.builder().id("p2").nombre("P2").stock(50).build();
        Sucursal s1 = Sucursal.builder().id("s1").nombre("Suc1").productos(new ArrayList<>(List.of(p1, p2))).build();

        Producto p3 = Producto.builder().id("p3").nombre("P3").stock(100).build();
        Producto p4 = Producto.builder().id("p4").nombre("P4").stock(20).build();
        Sucursal s2 = Sucursal.builder().id("s2").nombre("Suc2").productos(new ArrayList<>(List.of(p3, p4))).build();

        baseFranquicia.setSucursales(new ArrayList<>(List.of(s1, s2)));

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));

        StepVerifier.create(franquiciaService.obtenerTopProductosPorSucursal(franquiciaId))
                .expectNextMatches(dto -> dto.getSucursal().equals("Suc1") && dto.getProducto().equals("P2") && dto.getStock() == 50)
                .expectNextMatches(dto -> dto.getSucursal().equals("Suc2") && dto.getProducto().equals("P3") && dto.getStock() == 100)
                .verifyComplete();
    }

    @Test
    void actualizarNombreFranquicia_ShouldUpdateName() {
        String franquiciaId = "f-123";
        String nuevoNombre = "Updated Name";
        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.actualizarNombreFranquicia(franquiciaId, nuevoNombre))
                .expectNextMatches(f -> f.getNombre().equals(nuevoNombre))
                .verifyComplete();
    }

    @Test
    void actualizarNombreSucursal_ShouldUpdateSucursalName() {
        String franquiciaId = "f-123";
        String sucursalId = "s-456";
        String nuevoNombre = "New Sucursal Name";
        Sucursal sucursal = Sucursal.builder().id(sucursalId).nombre("Old Name").build();
        baseFranquicia.setSucursales(new ArrayList<>(List.of(sucursal)));

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.actualizarNombreSucursal(franquiciaId, sucursalId, nuevoNombre))
                .expectNextMatches(f -> f.getSucursales().get(0).getNombre().equals(nuevoNombre))
                .verifyComplete();
    }

    @Test
    void actualizarNombreProducto_ShouldUpdateProductoName() {
        String franquiciaId = "f-123";
        String productoId = "p-789";
        String nuevoNombre = "New Prod Name";
        Producto producto = Producto.builder().id(productoId).nombre("Old Name").stock(10).build();
        Sucursal sucursal = Sucursal.builder().id("s1").nombre("Suc").productos(new ArrayList<>(List.of(producto))).build();
        baseFranquicia.setSucursales(new ArrayList<>(List.of(sucursal)));

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(franquiciaService.actualizarNombreProducto(franquiciaId, productoId, nuevoNombre))
                .expectNextMatches(f -> f.getSucursales().get(0).getProductos().get(0).getNombre().equals(nuevoNombre))
                .verifyComplete();
    }

    @Test
    void actualizarNombreSucursal_WhenSucursalNotFound_ShouldReturnError() {
        String franquiciaId = "f-123";
        String sucursalId = "not-found";
        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));

        StepVerifier.create(franquiciaService.actualizarNombreSucursal(franquiciaId, sucursalId, "New Name"))
                .expectErrorMatches(t -> t instanceof RuntimeException && t.getMessage().equals("Sucursal no encontrada"))
                .verify();
    }

    @Test
    void obtenerTopProductosPorSucursal_WhenSucursalHasNoProducts_ShouldFilterItOut() {
        String franquiciaId = "f-123";
        Sucursal s1 = Sucursal.builder().id("s1").nombre("Empty").productos(new ArrayList<>()).build();
        baseFranquicia.setSucursales(new ArrayList<>(List.of(s1)));

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Mono.just(baseFranquicia));

        StepVerifier.create(franquiciaService.obtenerTopProductosPorSucursal(franquiciaId))
                .expectNextCount(0)
                .verifyComplete();
    }
}

