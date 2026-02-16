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
@Table(name = "reportes")
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "estado")
    private String estado = "Pendiente";

    @Column(name = "imagen_url")
    private String imagenUrl;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "condominio_id")
    private Condominio condominio;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "tipo")
    private String tipo; // 'urgente', 'informativo'

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }
}
