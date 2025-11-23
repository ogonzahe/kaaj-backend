package com.kaaj.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitaRequest {
    private String nombreVisitante;
    private String apellidoVisitado;
    private String condominio;
    private String tipoAcceso;
    private LocalDate fechaProgramada;
    private LocalTime horaProgramada;
    private Integer usuarioId;

    // Getters y Setters
    public String getNombreVisitante() {
        return nombreVisitante;
    }

    public void setNombreVisitante(String nombreVisitante) {
        this.nombreVisitante = nombreVisitante;
    }

    public String getApellidoVisitado() {
        return apellidoVisitado;
    }

    public void setApellidoVisitado(String apellidoVisitado) {
        this.apellidoVisitado = apellidoVisitado;
    }

    public String getCondominio() {
        return condominio;
    }

    public void setCondominio(String condominio) {
        this.condominio = condominio;
    }

    public String getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(String tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalTime getHoraProgramada() {
        return horaProgramada;
    }

    public void setHoraProgramada(LocalTime horaProgramada) {
        this.horaProgramada = horaProgramada;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}