package com.kaaj.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoriaDocumentoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String color;
    private Long condominioId;
}
