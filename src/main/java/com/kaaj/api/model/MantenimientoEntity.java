package com.kaaj.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mantenimiento")
public class MantenimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Integer idMantenimiento;

    @Column(name = "titulo_reporte", nullable = false, length = 60)
    private String tituloReporte;

    @Column(name = "usuario_apartamento", nullable = false, length = 100)
    private String usuarioApartamento;

    @Column(name = "mensaje", nullable = false, length = 500)
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

    @Column(name = "condominio_id")
    private Integer condominioId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "numero_casa", length = 20)
    private String numeroCasa;
}
