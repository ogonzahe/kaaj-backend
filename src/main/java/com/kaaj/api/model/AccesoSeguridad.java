package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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
}
