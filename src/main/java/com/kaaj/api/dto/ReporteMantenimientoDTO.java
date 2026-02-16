package com.kaaj.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReporteMantenimientoDTO {
    private String tituloReporte;
    private String descripcion;
    private Integer idTipo;
    private String usuario;
    private String ubicacion;
    private Integer condominioId;
    private String numeroCasa;
    private Integer usuarioId;
    private String estatus;

    // Constructor con parámetros
    public ReporteMantenimientoDTO(String tituloReporte, String descripcion, Integer idTipo,
                                  String usuario, String ubicacion, Integer condominioId,
                                  String numeroCasa, Integer usuarioId, String estatus) {
        this.tituloReporte = tituloReporte;
        this.descripcion = descripcion;
        this.idTipo = idTipo;
        this.usuario = usuario;
        this.ubicacion = ubicacion;
        this.condominioId = condominioId;
        this.numeroCasa = numeroCasa;
        this.usuarioId = usuarioId;
        this.estatus = estatus;
    }
}
