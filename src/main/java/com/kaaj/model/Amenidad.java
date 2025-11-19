package com.kaaj.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "amenidades")
@Data // Esto genera automáticamente los Getters y Setters (gracias a Lombok)
public class Amenidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private Integer capacidad;

    private Boolean activa;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "creada_en")
    private LocalDateTime creadaEn;

    // Esto pone la fecha y hora automática antes de guardar
    @PrePersist
    protected void onCreate() {
        creadaEn = LocalDateTime.now();
    }
}