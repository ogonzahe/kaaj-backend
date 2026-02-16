package com.kaaj.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VisitaResponse {
    private Integer id;
    private String codigoQr;
    private String nombreVisitante;
    private String apellidoVisitado;
    private String condominio;
    private String tipoAcceso;
    private LocalDate fechaProgramada;
    private LocalTime horaProgramada;
    private String estado;
    private LocalDateTime fechaUtilizacion;
    private LocalDateTime creadoEn;
}
