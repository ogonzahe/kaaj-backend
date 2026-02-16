package com.kaaj.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "movimientos_pagos")
public class MovimientoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 100)
    private String concepto;

    @JsonProperty("saldo_anterior")
    @Column(name = "saldo_anterior", precision = 10, scale = 2)
    private BigDecimal saldoAnterior;

    @JsonProperty("saldo_nuevo")
    @Column(name = "saldo_nuevo", precision = 10, scale = 2)
    private BigDecimal saldoNuevo;

    @Column(length = 100)
    private String referencia;

    @JsonProperty("metodo_pago")
    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @JsonProperty("fecha_movimiento")
    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento;

    @JsonProperty("usuario_registro")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_registro")
    private Usuario usuarioRegistro;

    // Enums
    public enum TipoMovimiento {
        CARGO, ABONO
    }

    // Constructor
    public MovimientoPago() {
        this.fechaMovimiento = LocalDateTime.now();
    }
}
