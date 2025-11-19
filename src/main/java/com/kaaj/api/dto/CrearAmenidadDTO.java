package com.kaaj.api.dto;

import lombok.Data;

@Data
public class CrearAmenidadDTO {
    
    // Campos b
    private String nombre;
    private String descripcion;

    private Integer capacidad;
    private Boolean activa;
    private String imagenUrl;
}