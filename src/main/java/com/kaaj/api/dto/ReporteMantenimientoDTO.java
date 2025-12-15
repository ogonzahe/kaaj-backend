package com.kaaj.api.dto;


public class ReporteMantenimientoDTO {

    private String tituloReporte;

    private String usuarioApartamento;
    private String mensaje;
    private Integer idTipo; 

    public String getTituloReporte() {
        return tituloReporte;
    }

    public void setTituloReporte(String tituloReporte) {
        this.tituloReporte = tituloReporte;
    }

    public String getUsuarioApartamento() {
        return usuarioApartamento;
    }

    public void setUsuarioApartamento(String usuarioApartamento) {
        this.usuarioApartamento = usuarioApartamento;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }
}