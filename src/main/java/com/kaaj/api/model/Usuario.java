package com.kaaj.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "rol_id")
    private Integer rolId;

    @Column(name = "rol_nombre")
    private String rolNombre;

    @ManyToOne
    @JoinColumn(name = "condominio_id")
    private Condominio condominio;

    @ManyToOne
    @JoinColumn(name = "apartamento_id")
    private Apartamento apartamento;

    @Column(name = "numero_casa")
    private String numeroCasa;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacion> notificaciones = new ArrayList<>();

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public void setApartamento(Apartamento apartamento) {
        this.apartamento = apartamento;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getCondominioId() {
        return (condominio != null && condominio.getId() != null) ? condominio.getId() : null;
    }

    public void setCondominioId(Integer condominioId) {
        if (condominioId != null) {
            Condominio cond = new Condominio();
            cond.setId(condominioId);
            this.condominio = cond;
        } else {
            this.condominio = null;
        }
    }

    public boolean perteneceACondominio(Integer condominioId) {
        return (condominio != null && condominio.getId() != null &&
                condominio.getId().equals(condominioId));
    }

    public Integer getApartamentoId() {
        return (apartamento != null && apartamento.getId() != null) ? apartamento.getId() : null;
    }

    public String getDireccionApartamento() {
        if (apartamento != null) {
            return apartamento.getEdificio() + "-" + apartamento.getNumero();
        }
        return null;
    }

    public boolean esAdmin() {
        return "admin_usuario".equalsIgnoreCase(rolNombre) ||
               "ADMIN".equalsIgnoreCase(rolNombre);
    }

    public boolean esResidente() {
        return "USUARIO".equalsIgnoreCase(rolNombre) ||
               "RESIDENTE".equalsIgnoreCase(rolNombre);
    }

    public boolean esSeguridad() {
        return "SEGURIDAD".equalsIgnoreCase(rolNombre);
    }

    public boolean esCopropietario() {
        return "COPO".equalsIgnoreCase(rolNombre) ||
               "COPROPIETARIO".equalsIgnoreCase(rolNombre);
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    public boolean isValid() {
        return correo != null && !correo.trim().isEmpty() &&
               contrasena != null && !contrasena.trim().isEmpty();
    }
}