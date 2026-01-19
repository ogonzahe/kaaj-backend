package com.kaaj.api.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Time;

@Entity
@Table(name = "amenidades")
public class Amenidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    private Integer capacidad = 1;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('activa', 'inactiva') DEFAULT 'activa'")
    private EstadoAmenidad estado = EstadoAmenidad.activa;

    @Column(name = "hora_apertura")
    @JsonFormat(pattern = "HH:mm")
    private Time horaApertura;

    @Column(name = "hora_cierre")
    @JsonFormat(pattern = "HH:mm")
    private Time horaCierre;

    @Column(name = "dias_disponibles", columnDefinition = "JSON")
    private String diasDisponibles;

    @Column(name = "condominio_id")
    private Integer condominioId;

    @Column(name = "creado_en", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date creadoEn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoAmenidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoAmenidad estado) {
        this.estado = estado;
    }

    public Time getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(Time horaApertura) {
        this.horaApertura = horaApertura;
    }

    public Time getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(Time horaCierre) {
        this.horaCierre = horaCierre;
    }

    public String getDiasDisponibles() {
        return diasDisponibles;
    }

    public void setDiasDisponibles(String diasDisponibles) {
        this.diasDisponibles = diasDisponibles;
    }

    public Integer getCondominioId() {
        return condominioId;
    }

    public void setCondominioId(Integer condominioId) {
        this.condominioId = condominioId;
    }

    public java.util.Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(java.util.Date creadoEn) {
        this.creadoEn = creadoEn;
    }

    public enum EstadoAmenidad {
        activa, inactiva
    }
}