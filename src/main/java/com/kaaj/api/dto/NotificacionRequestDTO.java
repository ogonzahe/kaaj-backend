package com.kaaj.api.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificacionRequestDTO {
    private String titulo;
    private String descripcion;
    private String prioridad;
    private Integer usuarioId; // null = para todos los usuarios del condominio
    private Integer condominioId; // Para un condominio específico
    private List<Integer> condominiosIds; // Para múltiples condominios
    private Boolean enviarATodosCondominios; // Para todos los condominios del admin

    // Constructor con parámetros
    public NotificacionRequestDTO(String titulo, String descripcion, String prioridad,
                                 Integer usuarioId, Integer condominioId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.usuarioId = usuarioId;
        this.condominioId = condominioId;
    }
}
