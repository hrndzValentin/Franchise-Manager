package com.accenture.franchise.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearProductoRequest {

    @NotBlank
    private String nombre;

    @NotNull
    @Min(0)
    private Integer stock;
}