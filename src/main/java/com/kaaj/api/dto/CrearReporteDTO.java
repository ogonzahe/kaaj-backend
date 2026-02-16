package com.kaaj.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearReporteDTO {
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String tipo;
    private Long usuarioId;
    private Long condominioId;
}
