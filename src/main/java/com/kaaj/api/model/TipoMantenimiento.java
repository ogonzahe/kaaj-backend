package com.kaaj.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_mantenimiento")
public class TipoMantenimiento {

    @Id
    @Column(name = "id_tipo")
    private Integer idTipo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    // Getters y Setters
    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}