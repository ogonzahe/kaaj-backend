package com.kaaj.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

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

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaUtilizacion() {
        return fechaUtilizacion;
    }

    public void setFechaUtilizacion(LocalDateTime fechaUtilizacion) {
        this.fechaUtilizacion = fechaUtilizacion;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}