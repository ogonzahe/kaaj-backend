package com.kaaj.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearPagoProgramadoDTO {
    private String concepto;
    private String descripcion;
    private String categoria;
    private BigDecimal monto;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private List<Long> apartamentosIds;
    private Boolean esRecurrente;
    private Integer periodicidad;
    private Integer intervaloDias;
    private Integer repeticiones;
    private Long condominioId;
    private Long adminId;
    private String tipo;
}
