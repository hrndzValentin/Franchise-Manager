package com.accenture.franchise.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarStockRequest {

    @NotNull
    @Min(0)
    private Integer stock;
}