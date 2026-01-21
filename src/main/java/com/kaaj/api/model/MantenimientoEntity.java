package com.kaaj.api.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mantenimiento")
public class MantenimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Integer idMantenimiento;

    @Column(name = "titulo_reporte", nullable = false, length = 60)
    private String tituloReporte;

    @Column(name = "usuario_apartamento", nullable = false, length = 100)
    private String usuarioApartamento;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estatus", nullable = false)
    private EstatusMantenimientoEntity estatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoMantenimiento tipo;

    @Column(name = "fecha_alta", nullable = false)
    private Date fechaAlta;

    @Column(name = "fecha_mod")
    private Date fechaMod;

    @Column(name = "condominio_id")
    private Integer condominioId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "numero_casa", length = 20)
    private String numeroCasa;

    // Getters y Setters
    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public String getTituloReporte() {
        return tituloReporte;
    }

    public void setTituloReporte(String tituloReporte) {
        this.tituloReporte = tituloReporte;
    }

    public String getUsuarioApartamento() {
        return usuarioApartamento;
    }

    public void setUsuarioApartamento(String usuarioApartamento) {
        this.usuarioApartamento = usuarioApartamento;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        this.tipo = tipo;
    }

    public EstatusMantenimientoEntity getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusMantenimientoEntity estatus) {
        this.estatus = estatus;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public Integer getCondominioId() {
        return condominioId;
    }

    public void setCondominioId(Integer condominioId) {
        this.condominioId = condominioId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }
}