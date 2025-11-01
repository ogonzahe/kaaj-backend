package com.kaaj.api.mapper;

import com.kaaj.api.model.Notificacion;
import com.kaaj.api.dto.NotificacionDTO;

public class NotificacionMapper {

    public static NotificacionDTO toDTO(Notificacion n) {
        return new NotificacionDTO(
                n.getId(),
                n.getTitulo(),
                n.getDescripcion(),
                n.getPrioridad(),
                n.getLeida(),
                n.getCreadaEn(),
                n.getUsuario() != null ? n.getUsuario().getId() : null);
    }

    public static Notificacion toEntity(NotificacionDTO dto) {
        Notificacion n = new Notificacion();
        n.setId(dto.getId());
        n.setTitulo(dto.getTitulo());
        n.setDescripcion(dto.getDescripcion());
        n.setPrioridad(dto.getPrioridad());
        n.setLeida(dto.getLeida());
        n.setCreadaEn(dto.getCreadaEn());
        return n;
    }
}
