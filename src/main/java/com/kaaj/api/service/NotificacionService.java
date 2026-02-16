package com.kaaj.api.service;

import com.kaaj.api.dto.NotificacionRequestDTO;
import com.kaaj.api.model.Notificacion;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.model.UsuarioCondominio;
import com.kaaj.api.repository.NotificacionRepository;
import com.kaaj.api.repository.UsuarioRepository;
import com.kaaj.api.repository.CondominioRepository;
import com.kaaj.api.repository.UsuarioCondominioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CondominioRepository condominioRepository;
    private final UsuarioCondominioRepository usuarioCondominioRepository;

    // ========== CREAR NOTIFICACIONES ==========

    @Transactional
    public List<Notificacion> crearNotificacion(NotificacionRequestDTO dto, Integer adminId) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        // Validar que el admin tiene permiso sobre los condominios
        List<Condominio> condominiosPermitidos = obtenerCondominiosPermitidos(admin);

        // Determinar condominios destino
        List<Condominio> condominiosDestino = determinarCondominiosDestino(dto, admin, condominiosPermitidos);

        if (condominiosDestino.isEmpty()) {
            throw new RuntimeException("No se especificaron condominios destino válidos");
        }

        List<Notificacion> notificacionesCreadas = new ArrayList<>();

        for (Condominio condominio : condominiosDestino) {
            if (dto.getUsuarioId() != null && dto.getUsuarioId() > 0) {
                // Notificación para usuario específico
                Notificacion notificacion = crearNotificacionUsuarioEspecifico(dto, condominio);
                if (notificacion != null) {
                    notificacionesCreadas.add(notificacion);
                }
            } else {
                // Notificación para TODO el condominio (UNA sola notificación)
                Notificacion notificacion = crearNotificacionParaCondominio(dto, condominio);
                if (notificacion != null) {
                    notificacionesCreadas.add(notificacion);
                }
            }
        }

        return notificacionesCreadas;
    }

    private List<Condominio> determinarCondominiosDestino(NotificacionRequestDTO dto, Usuario admin,
                                                          List<Condominio> condominiosPermitidos) {
        List<Condominio> condominiosDestino = new ArrayList<>();

        if (dto.getEnviarATodosCondominios() != null && dto.getEnviarATodosCondominios()) {
            // Enviar a TODOS los condominios permitidos del admin
            condominiosDestino.addAll(condominiosPermitidos);
        } else if (dto.getCondominiosIds() != null && !dto.getCondominiosIds().isEmpty()) {
            // Enviar a condominios específicos (validar que están permitidos)
            for (Integer condominioId : dto.getCondominiosIds()) {
                Condominio condominio = condominiosPermitidos.stream()
                        .filter(c -> c.getId().equals(condominioId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No tienes permiso para el condominio ID: " + condominioId));
                condominiosDestino.add(condominio);
            }
        } else if (dto.getCondominioId() != null) {
            // Enviar a un condominio específico
            Condominio condominio = condominiosPermitidos.stream()
                    .filter(c -> c.getId().equals(dto.getCondominioId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No tienes permiso para el condominio ID: " + dto.getCondominioId()));
            condominiosDestino.add(condominio);
        } else {
            // Por defecto: condominio principal del admin
            if (admin.getCondominio() != null) {
                condominiosDestino.add(admin.getCondominio());
            } else {
                throw new RuntimeException("El admin no tiene condominio asignado");
            }
        }

        return condominiosDestino;
    }

    private List<Condominio> obtenerCondominiosPermitidos(Usuario admin) {
        List<Condominio> condominios = new ArrayList<>();

        // 1. Buscar en la tabla UsuarioCondominio (para admins con múltiples condominios)
        List<UsuarioCondominio> asignaciones = usuarioCondominioRepository.findByUsuarioId(admin.getId());

        if (!asignaciones.isEmpty()) {
            // Admin tiene múltiples condominios asignados
            for (UsuarioCondominio asignacion : asignaciones) {
                if (asignacion.getCondominio() != null) {
                    condominios.add(asignacion.getCondominio());
                }
            }
        } else {
            // Admin viejo (solo un condominio)
            if (admin.getCondominio() != null) {
                condominios.add(admin.getCondominio());
            }
        }

        return condominios;
    }

    private Notificacion crearNotificacionUsuarioEspecifico(NotificacionRequestDTO dto, Condominio condominio) {
        Usuario usuarioDestino = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario destino no encontrado"));

        // Verificar que el usuario pertenece al condominio
        if (!usuarioDestino.getCondominioId().equals(condominio.getId())) {
            throw new RuntimeException("El usuario destino no pertenece al condominio: " + condominio.getNombre());
        }

        // Verificar que el usuario NO es admin (rol_id 1 o 4)
        Integer rolId = usuarioDestino.getRolId();
        if (rolId != null && (rolId == 1 || rolId == 4)) {
            throw new RuntimeException("No se pueden enviar notificaciones a administradores o COPOs");
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuarioDestino);
        notificacion.setCondominio(condominio);
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setDescripcion(dto.getDescripcion());
        notificacion.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : "INFORMATIVO");
        notificacion.setLeida(false);
        notificacion.setCreadaEn(new Timestamp(System.currentTimeMillis()));

        return notificacionRepository.save(notificacion);
    }

    private Notificacion crearNotificacionParaCondominio(NotificacionRequestDTO dto, Condominio condominio) {
        // Obtener todos los usuarios del condominio
        List<Usuario> usuariosCondominio = usuarioRepository.findByCondominioId(condominio.getId());

        // Filtrar solo usuarios con rol_id 2 (USUARIO) o 3 (SEGURIDAD)
        List<Usuario> usuariosPermitidos = usuariosCondominio.stream()
                .filter(usuario -> {
                    Integer rolId = usuario.getRolId();
                    return rolId != null && (rolId == 2 || rolId == 3); // USUARIO (2) o SEGURIDAD (3)
                })
                .collect(Collectors.toList());

        if (usuariosPermitidos.isEmpty()) {
            throw new RuntimeException("No hay residentes o personal de seguridad en el condominio: " + condominio.getNombre());
        }

        // Crear UNA sola notificación para todo el condominio
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(null); // null = notificación general para todo el condominio
        notificacion.setCondominio(condominio);
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setDescripcion(dto.getDescripcion());
        notificacion.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : "INFORMATIVO");
        notificacion.setLeida(false);
        notificacion.setCreadaEn(new Timestamp(System.currentTimeMillis()));

        return notificacionRepository.save(notificacion);
    }

    // ========== OBTENER NOTIFICACIONES ==========

    public List<Notificacion> obtenerTodasNotificaciones(List<Integer> condominiosIds) {
        if (condominiosIds == null || condominiosIds.isEmpty()) {
            throw new RuntimeException("Debe especificar al menos un condominio");
        }
        return notificacionRepository.findByCondominioIdIn(condominiosIds);
    }

    public List<Notificacion> obtenerNotificacionesUsuario(Integer usuarioId, Integer condominioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el usuario pertenece al condominio
        if (!usuario.getCondominioId().equals(condominioId)) {
            throw new RuntimeException("El usuario no pertenece al condominio especificado");
        }

        return notificacionRepository.findByUsuarioIdAndCondominioId(usuarioId, condominioId);
    }

    public List<Notificacion> obtenerNotificacionesNoLeidas(Integer condominioId) {
        return notificacionRepository.findByCondominioIdAndLeidaFalse(condominioId);
    }

    // ========== ACTUALIZAR NOTIFICACIONES ==========

    @Transactional
    public Notificacion marcarComoLeida(Integer notificacionId, Integer condominioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // Verificar que la notificación pertenece al condominio
        if (notificacion.getCondominio() == null ||
            !notificacion.getCondominio().getId().equals(condominioId)) {
            throw new RuntimeException("No tienes permiso para modificar esta notificación");
        }

        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }

    // ========== ELIMINAR NOTIFICACIONES ==========

    @Transactional
    public void eliminarNotificacion(Integer notificacionId, Integer condominioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // Verificar que la notificación pertenece al condominio
        if (notificacion.getCondominio() == null ||
            !notificacion.getCondominio().getId().equals(condominioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta notificación");
        }

        notificacionRepository.delete(notificacion);
    }

    // ========== MÉTODOS AUXILIARES ==========

    public List<Notificacion> obtenerNotificacionesGlobales(Integer condominioId) {
        return notificacionRepository.findByCondominioIdAndUsuarioIsNull(condominioId);
    }

    public Long contarNotificacionesNoLeidas(Integer condominioId) {
        return notificacionRepository.countByCondominioIdAndLeidaFalse(condominioId);
    }

    public List<Notificacion> obtenerNotificacionesPorCondominioOrdenadas(Integer condominioId) {
        return notificacionRepository.findByCondominioIdOrderByCreadaEnDesc(condominioId);
    }

    // Método para verificar si admin tiene acceso a un condominio
    public boolean adminTieneAccesoACondominio(Integer adminId, Integer condominioId) {
        List<Condominio> condominiosPermitidos = obtenerCondominiosPermitidos(
            usuarioRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"))
        );

        return condominiosPermitidos.stream()
                .anyMatch(c -> c.getId().equals(condominioId));
    }
}