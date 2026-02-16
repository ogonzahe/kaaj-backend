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
@Table(name = "escaneos_qr")
public class EscaneoQr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "visita_id", nullable = false)
    private Visita visita;

    @Column(name = "fecha_escaneo")
    private LocalDateTime fechaEscaneo = LocalDateTime.now();

    @Column(name = "dispositivo")
    private String dispositivo;

    @Column(name = "ubicacion")
    private String ubicacion;
}
