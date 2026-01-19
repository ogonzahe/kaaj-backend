package com.kaaj.api.dto;

import java.util.List;

public class NotificacionRequestDTO {
    private String titulo;
    private String descripcion;
    private String prioridad;
    private Integer usuarioId; // null = para todos los usuarios del condominio
    private Integer condominioId; // Para un condominio específico
    private List<Integer> condominiosIds; // Para múltiples condominios
    private Boolean enviarATodosCondominios; // Para todos los condominios del admin

    // Constructor vacío
    public NotificacionRequestDTO() {
    }

    // Constructor con parámetros
    public NotificacionRequestDTO(String titulo, String descripcion, String prioridad,
                                 Integer usuarioId, Integer condominioId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.usuarioId = usuarioId;
        this.condominioId = condominioId;
    }

    // Getters y Setters
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

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getCondominioId() {
        return condominioId;
    }

    public void setCondominioId(Integer condominioId) {
        this.condominioId = condominioId;
    }

    public List<Integer> getCondominiosIds() {
        return condominiosIds;
    }

    public void setCondominiosIds(List<Integer> condominiosIds) {
        this.condominiosIds = condominiosIds;
    }

    public Boolean getEnviarATodosCondominios() {
        return enviarATodosCondominios;
    }

    public void setEnviarATodosCondominios(Boolean enviarATodosCondominios) {
        this.enviarATodosCondominios = enviarATodosCondominios;
    }
}