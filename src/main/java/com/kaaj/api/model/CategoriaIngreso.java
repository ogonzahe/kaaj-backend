package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categorias_ingresos")
public class CategoriaIngreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private String color;
    @ManyToOne
    @JoinColumn(name = "condominio_id")
    private Condominio condominio;
    @OneToMany(mappedBy = "categoria")
    private List<Ingreso> ingresos;
}
