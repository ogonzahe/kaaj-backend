package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pagos_programados")
public class PagoProgramado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String concepto;
    private String descripcion;
    private String categoria;
    private BigDecimal monto;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Column(name = "es_recurrente")
    private Boolean esRecurrente = false;

    private Integer periodicidad; // dias
    private Integer intervaloDias;

    // NUEVO CAMPO: repeticiones
    private Integer repeticiones = 1;

    @ManyToOne
    @JoinColumn(name = "condominio_id")
    private Condominio condominio;

    @ManyToMany
    @JoinTable(name = "pago_apartamentos",
               joinColumns = @JoinColumn(name = "pago_id"),
               inverseJoinColumns = @JoinColumn(name = "apartamento_id"))
    private List<Apartamento> apartamentos;

    // NUEVO CAMPO: Lista de IDs de usuarios (alternativa a apartamentos)
    @ElementCollection
    @CollectionTable(name = "pago_programado_usuarios",
                     joinColumns = @JoinColumn(name = "pago_programado_id"))
    @Column(name = "usuario_id")
    private List<Integer> usuariosIds = new ArrayList<>();

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
