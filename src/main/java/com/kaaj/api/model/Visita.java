package com.kaaj.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitas")
public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_qr", unique = true, nullable = false)
    private String codigoQr;

    @Column(name = "nombre_visitante", nullable = false)
    private String nombreVisitante;

    @Column(name = "apellido_visitado", nullable = false)
    private String apellidoVisitado;

    @Column(name = "condominio", nullable = false)
    private String condominio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acceso", nullable = false)
    private TipoAcceso tipoAcceso;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "hora_programada", nullable = false)
    private LocalTime horaProgramada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoVisita estado = EstadoVisita.Generado;

    @Column(name = "fecha_utilizacion")
    private LocalDateTime fechaUtilizacion;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Enums
    public enum TipoAcceso {
        Visitante, Contratista, Entrega, Personal
    }

    public enum EstadoVisita {
        Generado, Utilizado, Expirado
    }

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

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(TipoAcceso tipoAcceso) {
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

    public EstadoVisita getEstado() {
        return estado;
    }

    public void setEstado(EstadoVisita estado) {
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}