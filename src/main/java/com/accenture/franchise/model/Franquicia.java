package com.accenture.franchise.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "franquicias")
public class Franquicia {

    @Id
    private String id;

    private String nombre;

    @Builder.Default
    private List<Sucursal> sucursales = new ArrayList<>();
}