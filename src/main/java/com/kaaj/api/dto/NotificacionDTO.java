package com.kaaj.api.dto;

import java.time.LocalDateTime;

public class NotificacionDTO {

    private Integer id;
    private String titulo;
    private String descripcion;
    private String prioridad;
    private Boolean leida;
    private LocalDateTime creadaEn;
    private Integer usuarioId;

    public NotificacionDTO() {
    }

    public NotificacionDTO(Integer id, String titulo, String descripcion, String prioridad,
            Boolean leida, LocalDateTime creadaEn, Integer usuarioId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.leida = leida;
        this.creadaEn = creadaEn;
        this.usuarioId = usuarioId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}
