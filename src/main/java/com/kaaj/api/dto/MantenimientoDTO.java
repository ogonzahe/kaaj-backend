package com.kaaj.api.dto;
import java.util.Date;

public class MantenimientoDTO {
    private Integer idMantenimiento; 
    
    private Date fecha; 
    
    private String reporte;
    
    private String mensaje;
    private String tipo;      
    private String estatus;   

    public MantenimientoDTO(Integer idMantenimiento, Date fecha, String reporte, String mensaje, String tipo, String estatus) {
        this.idMantenimiento = idMantenimiento;
        this.fecha = fecha;
        this.reporte = reporte;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.estatus = estatus;
    }
    public MantenimientoDTO() {
    }

    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}