package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "amenidades")
public class Amenidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
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

    public enum EstadoAmenidad {
        activa, inactiva
    }
}
