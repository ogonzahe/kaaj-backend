package com.kaaj.api.dto;

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

    // Constructor vacío
    public ReporteMantenimientoDTO() {}

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

    // Getters y Setters
    public String getTituloReporte() {
        return tituloReporte;
    }

    public void setTituloReporte(String tituloReporte) {
        this.tituloReporte = tituloReporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getCondominioId() {
        return condominioId;
    }

    public void setCondominioId(Integer condominioId) {
        this.condominioId = condominioId;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}