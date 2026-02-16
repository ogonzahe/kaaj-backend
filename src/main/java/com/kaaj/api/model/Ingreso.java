package com.kaaj.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ingresos")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concepto;
    private String descripcion;
    private BigDecimal monto;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "mes")
    private Integer mes;

    // SOLUCION: Usar "anio" en Java pero mapear a "ano" en la BD
    @Column(name = "año")
    private Integer anio;

    @Column(name = "comprobante_url")
    private String comprobanteUrl;

    private String estatus;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CategoriaIngreso categoria;

    @ManyToOne
    @JoinColumn(name = "condominio_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Condominio condominio;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    // Metodo para JSON - retornar como "ano"
    public Integer getAño() { return anio; }
    public void setAño(Integer año) { this.anio = año; }

    // Pre-persist y pre-update
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
