package com.kaaj.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    // ========== UTILITY METHODS ==========

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
