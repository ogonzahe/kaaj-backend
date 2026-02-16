package com.kaaj.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VisitaRequest {
    private String nombreVisitante;
    private String apellidoVisitado;
    private String condominio;
    private String tipoAcceso;
    private LocalDate fechaProgramada;
    private LocalTime horaProgramada;
    private Integer usuarioId;
}
