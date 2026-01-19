package com.kaaj.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true) // nullable = true para notificaciones globales
    @JsonIgnore // Evitar recursión infinita en JSON
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "condominio_id")  // NUEVO: Relación con condominio
    private Condominio condominio;

    private String titulo;
    private String descripcion;
    private String prioridad;

    @Column(name = "leida")
    private Boolean leida;

    @Column(name = "creada_en")
    private Timestamp creadaEn;

    @PrePersist
    public void prePersist() {
        if (leida == null)
            leida = false;
        if (creadaEn == null)
            creadaEn = new Timestamp(System.currentTimeMillis());
        if (prioridad == null || prioridad.isBlank())
            prioridad = "INFORMATIVO";
    }

    @Transient
    @JsonProperty("fecha_creacion")
    public Timestamp getFechaCreacion() {
        return creadaEn;
    }

    // ========== MÉTODOS UTILITARIOS PARA LA RELACIÓN ==========

    @JsonProperty("usuario_id")
    public Integer getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    @JsonProperty("usuario_nombre")
    public String getUsuarioNombre() {
        return usuario != null ? usuario.getNombre() : null;
    }

    // ========== MÉTODO PARA LIMPIAR RELACIÓN ==========
    public void clearUsuario() {
        this.usuario = null;
    }

    // ========== GETTERS Y SETTERS COMPLETOS ==========
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public Timestamp getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(Timestamp creadaEn) {
        this.creadaEn = creadaEn;
    }
}