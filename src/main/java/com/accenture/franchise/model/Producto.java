package com.accenture.franchise.model;
import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {

    private String id;
    private String nombre;
    private Integer stock;
}