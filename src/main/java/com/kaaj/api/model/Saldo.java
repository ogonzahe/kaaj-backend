package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "saldos")
public class Saldo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // NUEVA RELACION CON CONDOMINIO
    @ManyToOne
    @JoinColumn(name = "condominio_id")
    private Condominio condominio;

    private BigDecimal monto;
    private String concepto;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    private Boolean pagado;

    @Column(name = "actualizado_en")
    private Timestamp actualizadoEn;

    @Column(name = "tipo_pago")
    private String tipoPago;

    @Column(name = "metodo_pago")
    private String metodoPago;

    private String referencia;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    private String descripcion;

    @Column(name = "saldo_actual")
    private BigDecimal saldoActual;

    @Column(name = "ultimo_movimiento")
    private Timestamp ultimoMovimiento;

    @Column(name = "referencia_factura")
    private String referenciaFactura;

    // NUEVOS CAMPOS PARA PAGOS RECURRENTES
    private String categoria;

    @Column(name = "es_recurrente")
    private Boolean esRecurrente = false;

    @Column(name = "numero_repeticion")
    private Integer numeroRepeticion;

    @Column(name = "pago_programado_id")
    private Integer pagoProgramadoId;

    @Column(name = "fecha_pago_completado")
    private LocalDateTime fechaPagoCompletado;
}
