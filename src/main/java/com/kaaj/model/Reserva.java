package com.kaaj.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import lombok.Data;

@Data
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "amenidad")
    private String amenidad;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name = "hora_reserva")
    private LocalTime horaReserva;

    @Column(name = "estado")
    private String estado;

    @Column(name = "creada_en")
    private Instant creadaEn;
}