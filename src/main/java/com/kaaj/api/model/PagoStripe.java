package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pagos_stripe")
public class PagoStripe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "saldos_id")
    private Saldo saldo;

    @Column(name = "stripe_payment_id")
    private String stripePaymentId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    private BigDecimal monto;
    private String moneda;
    private String concepto;
    private String estado;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "ultimos_4_digitos")
    private String ultimos4Digitos;

    @Column(name = "marca_tarjeta")
    private String marcaTarjeta;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_mensaje")
    private String errorMensaje;

    @Column(name = "saldo_anterior")
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_nuevo")
    private BigDecimal saldoNuevo;

    @Column(name = "es_parcial")
    private Boolean esParcial;

    @Column(name = "comprobante_url")
    private String comprobanteUrl;

    @Column(name = "referencia_factura")
    private String referenciaFactura;
}
