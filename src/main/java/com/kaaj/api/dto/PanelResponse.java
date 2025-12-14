package com.kaaj.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PanelResponse {
    private BigDecimal saldo;
    private LocalDate fechaLimite;
    private List<String> notificaciones;
    private String amenidad;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private String rol;

    // Constructor CORREGIDO - 7 parámetros
    public PanelResponse(BigDecimal saldo,
                        LocalDate fechaLimite,
                        List<String> notificaciones,
                        String amenidad,
                        LocalDate fechaReserva,
                        LocalTime horaReserva,
                        String rol) {
        this.saldo = saldo;
        this.fechaLimite = fechaLimite;
        this.notificaciones = notificaciones;
        this.amenidad = amenidad;
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.rol = rol;
    }

    // Constructor vacío para JSON
    public PanelResponse() {
    }

    // Getters y Setters
    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public List<String> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<String> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public String getAmenidad() {
        return amenidad;
    }

    public void setAmenidad(String amenidad) {
        this.amenidad = amenidad;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(LocalTime horaReserva) {
        this.horaReserva = horaReserva;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}