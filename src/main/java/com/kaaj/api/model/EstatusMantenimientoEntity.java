package com.kaaj.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estatus_mantenimiento")
public class EstatusMantenimientoEntity {
    
    @Id
    @Column(name = "id_estatus")
    private Integer idEstatus; 

    @Column(name = "descripcion", nullable = false)
    private String descripcion; 
    public Integer getIdEstatus() {
        return idEstatus;
    }
    public void setIdEstatus(Integer idEstatus) {
        this.idEstatus = idEstatus;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public EstatusMantenimientoEntity() {}
}