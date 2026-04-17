package com.accenture.franchise.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarNombreRequest {
    @NotBlank
    private String nombre;
}