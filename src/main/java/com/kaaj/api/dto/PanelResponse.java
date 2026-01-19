package com.kaaj.api.dto;

import com.kaaj.api.model.Usuario;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Notificacion;
import com.kaaj.api.model.Reserva;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PanelResponse {
    // Campos para el dashboard del usuario
    private Usuario usuario;
    private List<Saldo> saldos;
    private List<Notificacion> notificaciones;
    private List<Reserva> reservas;

    // Campos originales para compatibilidad (si se usan en algún lugar)
    private BigDecimal saldo;
    private LocalDate fechaLimite;
    private List<String> notificacionesSimples;
    private String amenidad;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private String rol;

    // Constructor vacío para JSON
    public PanelResponse() {
    }

    // Getters y Setters para los nuevos campos
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Saldo> getSaldos() {
        return saldos;
    }

    public void setSaldos(List<Saldo> saldos) {
        this.saldos = saldos;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    // Getters y Setters originales (para mantener compatibilidad)
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

    public List<String> getNotificacionesSimples() {
        return notificacionesSimples;
    }

    public void setNotificacionesSimples(List<String> notificacionesSimples) {
        this.notificacionesSimples = notificacionesSimples;
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