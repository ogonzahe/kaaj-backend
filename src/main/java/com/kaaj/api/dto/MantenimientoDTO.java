package com.kaaj.api.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MantenimientoDTO {
    private Integer idMantenimiento;
    private Date fecha;
    private String reporte;
    private String mensaje;
    private String tipo;
    private String estatus;
    private String usuarioApartamento;
    private Integer condominioId;
    private Integer usuarioId;
    private String ubicacion;
    private String numeroCasa;

    public MantenimientoDTO(Integer idMantenimiento, Date fecha, String reporte, String mensaje,
                           String tipo, String estatus) {
        this.idMantenimiento = idMantenimiento;
        this.fecha = fecha;
        this.reporte = reporte;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.estatus = estatus;
    }

    // Constructor completo
    public MantenimientoDTO(Integer idMantenimiento, Date fecha, String reporte, String mensaje,
                           String tipo, String estatus, String usuarioApartamento,
                           Integer condominioId, Integer usuarioId, String ubicacion,
                           String numeroCasa) {
        this.idMantenimiento = idMantenimiento;
        this.fecha = fecha;
        this.reporte = reporte;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.estatus = estatus;
        this.usuarioApartamento = usuarioApartamento;
        this.condominioId = condominioId;
        this.usuarioId = usuarioId;
        this.ubicacion = ubicacion;
        this.numeroCasa = numeroCasa;
    }
}
