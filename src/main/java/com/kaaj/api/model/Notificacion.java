package com.kaaj.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true) // nullable = true para notificaciones globales
    @JsonIgnore // Evitar recursion infinita en JSON
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "condominio_id")  // NUEVO: Relacion con condominio
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

    // ========== METODOS UTILITARIOS PARA LA RELACION ==========

    @JsonProperty("usuario_id")
    public Integer getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    @JsonProperty("usuario_nombre")
    public String getUsuarioNombre() {
        return usuario != null ? usuario.getNombre() : null;
    }

    // ========== METODO PARA LIMPIAR RELACION ==========
    public void clearUsuario() {
        this.usuario = null;
    }
}
