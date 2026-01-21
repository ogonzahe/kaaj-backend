package com.kaaj.api.dto;
import java.util.Date;

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

    public MantenimientoDTO() {
    }

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

    // Getters y Setters
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

    public String getUsuarioApartamento() {
        return usuarioApartamento;
    }

    public void setUsuarioApartamento(String usuarioApartamento) {
        this.usuarioApartamento = usuarioApartamento;
    }

    public Integer getCondominioId() {
        return condominioId;
    }

    public void setCondominioId(Integer condominioId) {
        this.condominioId = condominioId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }
}