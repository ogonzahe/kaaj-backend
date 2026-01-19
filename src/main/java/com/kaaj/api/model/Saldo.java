package com.kaaj.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;

@Entity
@Table(name = "saldos")
public class Saldo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // NUEVA RELACIÓN CON CONDOMINIO
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

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // NUEVO GETTER/SETTER para condominio
    public Condominio getCondominio() {
        return condominio;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
    }

    public Timestamp getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Timestamp actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public Timestamp getUltimoMovimiento() {
        return ultimoMovimiento;
    }

    public void setUltimoMovimiento(Timestamp ultimoMovimiento) {
        this.ultimoMovimiento = ultimoMovimiento;
    }

    public String getReferenciaFactura() {
        return referenciaFactura;
    }

    public void setReferenciaFactura(String referenciaFactura) {
        this.referenciaFactura = referenciaFactura;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean isEsRecurrente() {
        return esRecurrente;
    }

    public void setEsRecurrente(Boolean esRecurrente) {
        this.esRecurrente = esRecurrente;
    }

    public Integer getNumeroRepeticion() {
        return numeroRepeticion;
    }

    public void setNumeroRepeticion(Integer numeroRepeticion) {
        this.numeroRepeticion = numeroRepeticion;
    }

    public Integer getPagoProgramadoId() {
        return pagoProgramadoId;
    }

    public void setPagoProgramadoId(Integer pagoProgramadoId) {
        this.pagoProgramadoId = pagoProgramadoId;
    }

    public LocalDateTime getFechaPagoCompletado() {
        return fechaPagoCompletado;
    }

    public void setFechaPagoCompletado(LocalDateTime fechaPagoCompletado) {
        this.fechaPagoCompletado = fechaPagoCompletado;
    }
}