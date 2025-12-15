package com.kaaj.api.model;

import java.sql.Timestamp;
import java.util.Date;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mantenimiento")
public class MantenimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento") 
    private Integer idMantenimiento; 

    @Column(name = "titulo_reporte", nullable = false, length = 60)
    private String tituloReporte; 

    @Column(name = "usuario_apartamento", nullable = false, length = 20)
    private String usuarioApartamento; 

    @Column(name = "mensaje", nullable = false, length = 60)
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
    
}
