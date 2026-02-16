package com.kaaj.api.controller;

import com.kaaj.api.dto.NotificacionRequestDTO;
import com.kaaj.api.model.Notificacion;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.model.UsuarioCondominio;
import com.kaaj.api.repository.NotificacionRepository;
import com.kaaj.api.repository.UsuarioRepository;
import com.kaaj.api.repository.UsuarioCondominioRepository;
import com.kaaj.api.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioCondominioRepository usuarioCondominioRepository;
    private final NotificacionRepository notificacionRepository;

    // ========== MÉTODOS PARA ADMIN ==========

    // 1. GET: Obtener todas las notificaciones (para admin con múltiples condominios)
    @GetMapping
    public ResponseEntity<?> obtenerTodasNotificacionesAdmin(
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId,
            @RequestParam(value = "condominiosIds", required = false) List<Integer> condominiosIds) {
        try {
            // Verificar que el usuario es administrador
            Usuario admin = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!admin.esAdmin()) {
                throw new RuntimeException("No tienes permisos de administrador");
            }

            // Si no se especifican condominios, usar todos los del admin
            if (condominiosIds == null || condominiosIds.isEmpty()) {
                // Obtener condominios del admin (MÚLTIPLES)
                condominiosIds = obtenerCondominiosDelAdmin(admin);
            }

            List<Notificacion> notificaciones = notificacionService.obtenerTodasNotificaciones(condominiosIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificaciones", notificaciones);
            response.put("total", notificaciones.size());
            response.put("condominios_ids", condominiosIds);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Forbidden: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 2. POST: Crear nueva notificación (soporta múltiples condominios)
    @PostMapping
    public ResponseEntity<?> crearNotificacion(
            @RequestBody NotificacionRequestDTO dto,
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            // Verificar que el usuario es administrador
            Usuario admin = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!admin.esAdmin()) {
                throw new RuntimeException("No tienes permisos de administrador");
            }

            // Validar campos requeridos
            if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty() ||
                    dto.getDescripcion() == null || dto.getDescripcion().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Título y descripción son requeridos");
                return ResponseEntity.badRequest().body(error);
            }

            // Si se especifica condominioId, verificar que el admin tiene acceso
            if (dto.getCondominioId() != null) {
                if (!notificacionService.adminTieneAccesoACondominio(usuarioId, dto.getCondominioId())) {
                    throw new RuntimeException("No tienes acceso al condominio especificado");
                }
            }

            List<Notificacion> notificacionesCreadas = notificacionService.crearNotificacion(dto, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación creada exitosamente");
            response.put("notificaciones_creadas", notificacionesCreadas.size());
            response.put("notificaciones", notificacionesCreadas);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.warn("Forbidden: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al crear notificación", e);
            error.put("message", "Error al crear notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 3. PUT: Marcar notificación como leída (PARA ADMIN)
    @PutMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener la notificación
            Notificacion notificacion = notificacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

            // Verificar permisos
            if (usuario.esAdmin()) {
                // Para admin: verificar que tiene acceso al condominio
                List<Integer> condominiosPermitidos = obtenerCondominiosDelAdmin(usuario);
                if (!condominiosPermitidos.contains(notificacion.getCondominio().getId())) {
                    throw new RuntimeException("No tienes permiso para modificar esta notificación");
                }
            } else {
                // Para usuario normal: verificar que la notificación es para él
                if (notificacion.getUsuario() != null && !notificacion.getUsuario().getId().equals(usuarioId)) {
                    throw new RuntimeException("Esta notificación no es para ti");
                }
            }

            // Marcar como leída
            notificacion.setLeida(true);
            notificacionRepository.save(notificacion);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación marcada como leída");
            response.put("notificacion", notificacion);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Forbidden: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al marcar notificación como leída", e);
            error.put("message", "Error al marcar notificación como leída");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 4. DELETE: Eliminar notificación
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNotificacion(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            Usuario admin = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!admin.esAdmin()) {
                throw new RuntimeException("No tienes permisos de administrador");
            }

            // Obtener la notificación primero para saber a qué condominio pertenece
            Notificacion notificacion = notificacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

            // Verificar que el admin tiene acceso al condominio de la notificación
            List<Integer> condominiosPermitidos = obtenerCondominiosDelAdmin(admin);
            if (!condominiosPermitidos.contains(notificacion.getCondominio().getId())) {
                throw new RuntimeException("No tienes permiso para eliminar esta notificación");
            }

            notificacionService.eliminarNotificacion(id, notificacion.getCondominio().getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación eliminada exitosamente");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Forbidden: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al eliminar notificación", e);
            error.put("message", "Error al eliminar notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========== MÉTODOS PARA USUARIOS/RESIDENTES ==========

    // 5. GET: Obtener mis notificaciones (para residentes) - CORREGIDO
    @GetMapping("/mis-notificaciones")
    public ResponseEntity<?> obtenerMisNotificaciones(
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Integer condominioId = usuario.getCondominioId();
            if (condominioId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("notificaciones", new ArrayList<>());
                response.put("total", 0);
                return ResponseEntity.ok(response);
            }

            // Obtener notificaciones del condominio del usuario
            List<Notificacion> todasNotificaciones = notificacionRepository.findByCondominioId(condominioId);

            // Filtrar: notificaciones generales (sin usuario) o específicas para este usuario
            List<Notificacion> misNotificaciones = todasNotificaciones.stream()
                    .filter(notif -> notif.getUsuario() == null ||
                            notif.getUsuario().getId().equals(usuarioId))
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificaciones", misNotificaciones);
            response.put("total", misNotificaciones.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al obtener mis notificaciones", e);
            error.put("message", "Error al obtener notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 6. GET: Obtener notificaciones no leídas (para frontend) - CORREGIDO
    @GetMapping("/no-leidas")
    public ResponseEntity<?> obtenerNotificacionesNoLeidas(
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Integer condominioId = usuario.getCondominioId();
            if (condominioId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("notificaciones", new ArrayList<>());
                response.put("total", 0);
                return ResponseEntity.ok(response);
            }

            // Obtener todas las notificaciones del condominio
            List<Notificacion> todasNotificaciones = notificacionRepository.findByCondominioId(condominioId);

            // Filtrar notificaciones no leídas para este usuario
            List<Notificacion> noLeidas = todasNotificaciones.stream()
                    .filter(notif -> {
                        // Verificar que la notificación es para este usuario
                        boolean esParaUsuario = notif.getUsuario() == null ||
                                                notif.getUsuario().getId().equals(usuarioId);
                        // Verificar que no está leída
                        boolean noLeida = !notif.getLeida();
                        return esParaUsuario && noLeida;
                    })
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificaciones", noLeidas);
            response.put("total", noLeidas.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al obtener notificaciones no leídas", e);
            error.put("message", "Error al obtener notificaciones no leídas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    // Método para obtener condominios del admin (CORREGIDO PARA MÚLTIPLES)
    private List<Integer> obtenerCondominiosDelAdmin(Usuario admin) {
        List<Integer> condominiosIds = new ArrayList<>();

        // 1. Buscar en UsuarioCondominio (para múltiples condominios)
        List<UsuarioCondominio> asignaciones = usuarioCondominioRepository.findByUsuarioId(admin.getId());

        if (!asignaciones.isEmpty()) {
            for (UsuarioCondominio asignacion : asignaciones) {
                if (asignacion.getCondominio() != null) {
                    condominiosIds.add(asignacion.getCondominio().getId());
                }
            }
        } else {
            // Admin viejo (solo un condominio)
            if (admin.getCondominioId() != null) {
                condominiosIds.add(admin.getCondominioId());
            }
        }

        return condominiosIds;
    }

    // 7. GET: Obtener notificaciones por condominio específico (CORREGIDO)
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<?> obtenerNotificacionesPorCondominio(
            @PathVariable Integer condominioId,
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            // Verificar que el usuario es administrador
            Usuario admin = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!admin.esAdmin()) {
                throw new RuntimeException("No tienes permisos de administrador");
            }

            // Verificar que el admin tiene acceso a este condominio (MÚLTIPLES)
            List<Integer> condominiosPermitidos = obtenerCondominiosDelAdmin(admin);
            if (!condominiosPermitidos.contains(condominioId)) {
                throw new RuntimeException("No tienes acceso a este condominio");
            }

            List<Notificacion> notificaciones = notificacionRepository.findByCondominioId(condominioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificaciones", notificaciones);
            response.put("total", notificaciones.size());
            response.put("condominio_id", condominioId);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Forbidden: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al obtener notificaciones por condominio", e);
            error.put("message", "Error al obtener notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 8. GET: Obtener estadísticas de notificaciones (CORREGIDO)
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            // Verificar que el usuario es administrador
            Usuario admin = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!admin.esAdmin()) {
                throw new RuntimeException("No tienes permisos de administrador");
            }

            // Obtener todos los condominios del admin
            List<Integer> condominiosIds = obtenerCondominiosDelAdmin(admin);

            long total = 0;
            long totalLeidas = 0;
            long totalNoLeidas = 0;

            for (Integer condominioId : condominiosIds) {
                List<Notificacion> notificaciones = notificacionRepository.findByCondominioId(condominioId);
                total += notificaciones.size();

                long leidas = notificaciones.stream().filter(Notificacion::getLeida).count();
                long noLeidas = notificaciones.size() - leidas;

                totalLeidas += leidas;
                totalNoLeidas += noLeidas;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", total);
            response.put("leidas", totalLeidas);
            response.put("no_leidas", totalNoLeidas);
            response.put("condominios_ids", condominiosIds);
            response.put("total_condominios", condominiosIds.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Forbidden: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al obtener estadísticas de notificaciones", e);
            error.put("message", "Error al obtener estadísticas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 9. GET: Obtener condominios del admin (para frontend)
    @GetMapping("/mis-condominios")
    public ResponseEntity<?> obtenerMisCondominios(
            @RequestHeader(value = "X-Usuario-Id") Integer usuarioId) {
        try {
            Usuario admin = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!admin.esAdmin()) {
                throw new RuntimeException("No tienes permisos de administrador");
            }

            List<UsuarioCondominio> asignaciones = usuarioCondominioRepository.findByUsuarioId(usuarioId);
            List<Map<String, Object>> condominiosData = new ArrayList<>();

            for (UsuarioCondominio asignacion : asignaciones) {
                if (asignacion.getCondominio() != null) {
                    Map<String, Object> condData = new HashMap<>();
                    condData.put("id", asignacion.getCondominio().getId());
                    condData.put("nombre", asignacion.getCondominio().getNombre());
                    condData.put("es_principal", asignacion.getEsPrincipal() ? 1 : 0);
                    condominiosData.add(condData);
                }
            }

            // Si no hay en UsuarioCondominio, usar el condominio principal
            if (condominiosData.isEmpty() && admin.getCondominio() != null) {
                Map<String, Object> condData = new HashMap<>();
                condData.put("id", admin.getCondominio().getId());
                condData.put("nombre", admin.getCondominio().getNombre());
                condData.put("es_principal", 1);
                condominiosData.add(condData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("condominios", condominiosData);
            response.put("total", condominiosData.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Forbidden: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            log.error("Error al obtener condominios del admin", e);
            error.put("message", "Error al obtener condominios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}