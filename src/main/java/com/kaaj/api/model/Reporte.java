package com.kaaj.api.model; // AJUSTADO

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    private String ubicacion;

    private String estado;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        if (estado == null) {
            estado = "Pendiente";
        }
    }
}