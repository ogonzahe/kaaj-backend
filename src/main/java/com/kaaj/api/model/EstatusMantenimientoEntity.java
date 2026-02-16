package com.kaaj.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "estatus_mantenimiento")
public class EstatusMantenimientoEntity {

    @Id
    @Column(name = "id_estatus")
    private Integer idEstatus;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;
}
