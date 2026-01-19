package com.kaaj.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "apartamentos")
public class Apartamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "condominio_id", nullable = false)
    private Condominio condominio;

    @Column(nullable = false, length = 50)
    private String edificio;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(length = 50)
    private String tipo = "Departamento";

    private BigDecimal area;

    private Integer habitaciones = 2;

    private Integer banos = 1;

    @ManyToOne
    @JoinColumn(name = "propietario_usuario_id")
    private Usuario propietario;

    @ManyToOne
    @JoinColumn(name = "inquilino_usuario_id")
    private Usuario inquilino;

    @Column(name = "esta_alquilado")
    private Boolean estaAlquilado = false;

    @Column(name = "esta_habitado")
    private Boolean estaHabitado = true;

    @Column(name = "cuota_mantenimiento_base")
    private BigDecimal cuotaMantenimientoBase = BigDecimal.ZERO;

    @Lob
    private String observaciones;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }

    public String getEdificio() { return edificio; }
    public void setEdificio(String edificio) { this.edificio = edificio; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public Integer getHabitaciones() { return habitaciones; }
    public void setHabitaciones(Integer habitaciones) { this.habitaciones = habitaciones; }

    public Integer getBanos() { return banos; }
    public void setBanos(Integer banos) { this.banos = banos; }

    public Usuario getPropietario() { return propietario; }
    public void setPropietario(Usuario propietario) { this.propietario = propietario; }

    public Usuario getInquilino() { return inquilino; }
    public void setInquilino(Usuario inquilino) { this.inquilino = inquilino; }

    public Boolean getEstaAlquilado() { return estaAlquilado; }
    public void setEstaAlquilado(Boolean estaAlquilado) { this.estaAlquilado = estaAlquilado; }

    public Boolean getEstaHabitado() { return estaHabitado; }
    public void setEstaHabitado(Boolean estaHabitado) { this.estaHabitado = estaHabitado; }

    public BigDecimal getCuotaMantenimientoBase() { return cuotaMantenimientoBase; }
    public void setCuotaMantenimientoBase(BigDecimal cuotaMantenimientoBase) {
        this.cuotaMantenimientoBase = cuotaMantenimientoBase;
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // M??todo de conveniencia
    public String getDireccionCompleta() {
        return edificio + "-" + numero;
    }
}