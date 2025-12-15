package com.kaaj.api.dto;

import lombok.Data;

@Data
public class CrearReporteDTO {
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String imagenUrl;
}