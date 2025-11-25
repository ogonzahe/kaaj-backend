package com.kaaj.api.model;

import jakarta.persistence.*;

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

    // Getters y Setters
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

    public enum EstadoAmenidad {
        activa, inactiva
    }
}