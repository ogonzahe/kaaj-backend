package com.kaaj.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String nombreArchivo;
    private Long tamanio;
    private String mimeType;
    private Long condominioId;
    private String condominioNombre;
    private Long categoriaId;
    private String categoriaNombre;
    private Boolean esPublico;
    private LocalDate fechaVigencia;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
