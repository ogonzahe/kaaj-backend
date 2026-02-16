package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tipo_mantenimiento")
public class TipoMantenimiento {

    @Id
    @Column(name = "id_tipo")
    private Integer idTipo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;
}
