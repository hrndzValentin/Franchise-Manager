package com.accenture.franchise.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductoDTO {
    private String sucursal;
    private String producto;
    private Integer stock;
}