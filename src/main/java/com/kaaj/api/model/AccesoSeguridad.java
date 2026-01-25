package com.kaaj.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "acceso_seguridad")
public class AccesoSeguridad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "visita_id", nullable = false)
    private Visita visita;

    @Column(name = "fecha_hora_acceso", nullable = false)
    private LocalDateTime fechaHoraAcceso;

    @Column(name = "guardia_id")
    private Integer guardiaId;

    @Column(name = "tipo_acceso")
    private String tipoAcceso; // ENTRADA, SALIDA

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Visita getVisita() {
        return visita;
    }

    public void setVisita(Visita visita) {
        this.visita = visita;
    }

    public LocalDateTime getFechaHoraAcceso() {
        return fechaHoraAcceso;
    }

    public void setFechaHoraAcceso(LocalDateTime fechaHoraAcceso) {
        this.fechaHoraAcceso = fechaHoraAcceso;
    }

    public Integer getGuardiaId() {
        return guardiaId;
    }

    public void setGuardiaId(Integer guardiaId) {
        this.guardiaId = guardiaId;
    }

    public String getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(String tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}