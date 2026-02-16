package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "condominios")
public class Condominio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String direccion;
    private String ciudad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "creado_en")
    @JsonProperty("creado_en")
    private LocalDateTime creadoEn;

    private String entidad;

    @Column(name = "numero_casas")
    @JsonProperty("numero_casas")
    private Integer numeroCasas;

    private String responsable;
    private String telefono;
    private String email;
    private String administracion;

    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }
}
