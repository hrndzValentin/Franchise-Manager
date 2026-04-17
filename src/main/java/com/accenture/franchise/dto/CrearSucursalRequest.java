package com.accenture.franchise.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearSucursalRequest {
    @NotBlank
    private String nombre;
}