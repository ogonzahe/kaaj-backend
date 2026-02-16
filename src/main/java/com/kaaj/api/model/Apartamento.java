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

    // Metodo de conveniencia
    public String getDireccionCompleta() {
        return edificio + "-" + numero;
    }
}
