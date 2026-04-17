package com.accenture.franchise.model;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sucursal {

    private String id;
    private String nombre;
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();
}