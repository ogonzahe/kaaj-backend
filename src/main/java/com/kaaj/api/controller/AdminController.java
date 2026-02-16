package com.kaaj.api.controller;

import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/administradores")
public class AdminController {

    private final UsuarioRepository usuarioRepo;
    private final CondominioRepository condominioRepo;
    private final UsuarioCondominioRepository usuarioCondominioRepo;

    // ========== OBTENER CONDÓMINOS DE UN ADMINISTRADOR ==========
    @GetMapping("/{adminId}/condominios")
    public ResponseEntity<?> getCondominiosPorAdmin(@PathVariable Integer adminId) {
        try {
            Optional<Usuario> adminOpt = usuarioRepo.findById(adminId);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario admin = adminOpt.get();
            List<Map<String, Object>> condominios = new ArrayList<>();

            // Obtener todos los condominios asignados (múltiples)
            List<UsuarioCondominio> asignaciones = usuarioCondominioRepo.findByUsuarioId(adminId);

            for (UsuarioCondominio asignacion : asignaciones) {
                if (asignacion.getCondominio() != null) {
                    Map<String, Object> condominioData = new HashMap<>();
                    condominioData.put("id", asignacion.getCondominio().getId());
                    condominioData.put("nombre", asignacion.getCondominio().getNombre());
                    condominioData.put("es_principal", asignacion.getEsPrincipal() ? 1 : 0);
                    condominios.add(condominioData);
                }
            }

            return ResponseEntity.ok(condominios);

        } catch (Exception e) {
            log.error("Error al obtener condominios del administrador con id: {}", adminId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener condominios del administrador"));
        }
    }

    // ========== ASIGNAR MÚLTIPLES CONDÓMINOS A UN ADMINISTRADOR ==========
    @PostMapping("/{adminId}/condominios")
    public ResponseEntity<?> asignarCondominios(
            @PathVariable Integer adminId,
            @RequestBody Map<String, Object> requestData) {
        try {
            Optional<Usuario> adminOpt = usuarioRepo.findById(adminId);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario admin = adminOpt.get();

            // Convertir los IDs a enteros
            List<Integer> condominiosIds = new ArrayList<>();
            if (requestData.get("condominios") instanceof List<?>) {
                for (Object obj : (List<?>) requestData.get("condominios")) {
                    if (obj instanceof Integer) {
                        condominiosIds.add((Integer) obj);
                    } else if (obj instanceof String) {
                        try {
                            condominiosIds.add(Integer.parseInt((String) obj));
                        } catch (NumberFormatException e) {
                            // Ignorar valores no numéricos
                        }
                    }
                }
            }

            Integer principalId = null;
            if (requestData.get("principal_id") != null) {
                try {
                    principalId = Integer.parseInt(requestData.get("principal_id").toString());
                } catch (NumberFormatException e) {
                    // Si no es número, intentar otras formas
                }
            }

            // Validaciones
            if (condominiosIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe seleccionar al menos un condominio"));
            }

            if (principalId == null) {
                principalId = condominiosIds.get(0); // Tomar el primero como principal
            }

            // Verificar que el principal esté en la lista
            if (!condominiosIds.contains(principalId)) {
                condominiosIds.add(principalId);
            }

            // 1. Eliminar asignaciones previas
            usuarioCondominioRepo.deleteByUsuarioId(adminId);

            // 2. Crear nuevas asignaciones
            List<UsuarioCondominio> nuevasAsignaciones = new ArrayList<>();
            for (Integer condominioId : condominiosIds) {
                Optional<Condominio> condominioOpt = condominioRepo.findById(condominioId);
                if (condominioOpt.isPresent()) {
                    UsuarioCondominio asignacion = new UsuarioCondominio();
                    asignacion.setUsuario(admin);
                    asignacion.setCondominio(condominioOpt.get());
                    asignacion.setEsPrincipal(condominioId.equals(principalId));
                    nuevasAsignaciones.add(asignacion);
                }
            }

            usuarioCondominioRepo.saveAll(nuevasAsignaciones);

            // 3. Actualizar condominio principal en el usuario (para compatibilidad)
            Optional<Condominio> condominioPrincipalOpt = condominioRepo.findById(principalId);
            if (condominioPrincipalOpt.isPresent()) {
                admin.setCondominio(condominioPrincipalOpt.get());
                usuarioRepo.save(admin);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Condominios asignados correctamente");
            response.put("adminId", adminId);
            response.put("condominiosAsignados", condominiosIds);
            response.put("condominioPrincipalId", principalId);
            response.put("totalCondominios", condominiosIds.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al asignar condominios al administrador con id: {}", adminId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al asignar condominios"));
        }
    }

    // ========== ELIMINAR TODAS LAS ASIGNACIONES ==========
    @DeleteMapping("/{adminId}/condominios")
    public ResponseEntity<?> eliminarAsignaciones(@PathVariable Integer adminId) {
        try {
            Optional<Usuario> adminOpt = usuarioRepo.findById(adminId);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Eliminar asignaciones
            List<UsuarioCondominio> asignaciones = usuarioCondominioRepo.findByUsuarioId(adminId);
            usuarioCondominioRepo.deleteAll(asignaciones);

            // Limpiar condominio principal
            Usuario admin = adminOpt.get();
            admin.setCondominio(null);
            usuarioRepo.save(admin);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Asignaciones eliminadas correctamente");
            response.put("adminId", adminId);
            response.put("asignacionesEliminadas", asignaciones.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al eliminar asignaciones del administrador con id: {}", adminId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar asignaciones"));
        }
    }

    // ========== OBTENER ADMINISTRADORES POR CONDÓMINO ==========
    @GetMapping("/por-condominio/{condominioId}")
    public ResponseEntity<?> getAdminsPorCondominio(@PathVariable Integer condominioId) {
        try {
            Optional<Condominio> condominioOpt = condominioRepo.findById(condominioId);
            if (condominioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<UsuarioCondominio> asignaciones = usuarioCondominioRepo.findByCondominioId(condominioId);
            List<Usuario> administradores = new ArrayList<>();

            for (UsuarioCondominio asignacion : asignaciones) {
                if (asignacion.getUsuario() != null &&
                        "admin_usuario".equalsIgnoreCase(asignacion.getUsuario().getRolNombre())) {
                    administradores.add(asignacion.getUsuario());
                }
            }

            return ResponseEntity.ok(administradores);

        } catch (Exception e) {
            log.error("Error al obtener administradores del condominio con id: {}", condominioId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener administradores del condominio"));
        }
    }

    // ========== OBTENER TODOS LOS ADMINISTRADORES ==========
    @GetMapping
    public ResponseEntity<?> getAllAdministradores() {
        try {
            List<Usuario> administradores = usuarioRepo.findByRolNombre("admin_usuario");

            List<Map<String, Object>> adminsConCondominios = new ArrayList<>();
            for (Usuario admin : administradores) {
                Map<String, Object> adminData = new HashMap<>();
                adminData.put("id", admin.getId());
                adminData.put("nombre", admin.getNombre());
                adminData.put("correo", admin.getCorreo());
                adminData.put("telefono", admin.getTelefono());
                adminData.put("activo", admin.getActivo());
                adminData.put("rolId", admin.getRolId());
                adminData.put("rolNombre", admin.getRolNombre());

                // Obtener condominios asignados (múltiples)
                List<UsuarioCondominio> asignaciones = usuarioCondominioRepo.findByUsuarioId(admin.getId());
                List<Integer> condominiosIds = new ArrayList<>();
                List<Map<String, Object>> condominiosData = new ArrayList<>();

                for (UsuarioCondominio asignacion : asignaciones) {
                    if (asignacion.getCondominio() != null) {
                        condominiosIds.add(asignacion.getCondominio().getId());

                        Map<String, Object> condData = new HashMap<>();
                        condData.put("id", asignacion.getCondominio().getId());
                        condData.put("nombre", asignacion.getCondominio().getNombre());
                        condData.put("es_principal", asignacion.getEsPrincipal() ? 1 : 0);
                        condominiosData.add(condData);
                    }
                }

                // Si no hay asignaciones pero tiene condominio principal (para compatibilidad)
                if (condominiosIds.isEmpty() && admin.getCondominio() != null) {
                    condominiosIds.add(admin.getCondominio().getId());

                    Map<String, Object> condData = new HashMap<>();
                    condData.put("id", admin.getCondominio().getId());
                    condData.put("nombre", admin.getCondominio().getNombre());
                    condData.put("es_principal", 1);
                    condominiosData.add(condData);
                }

                adminData.put("condominios_id", condominiosIds);
                adminData.put("condominios_data", condominiosData);
                adminData.put("condominioId", admin.getCondominio() != null ? admin.getCondominio().getId() : null);
                adminData.put("condominioNombre",
                        admin.getCondominio() != null ? admin.getCondominio().getNombre() : null);

                adminsConCondominios.add(adminData);
            }

            return ResponseEntity.ok(adminsConCondominios);

        } catch (Exception e) {
            log.error("Error al obtener administradores", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener administradores"));
        }
    }
}