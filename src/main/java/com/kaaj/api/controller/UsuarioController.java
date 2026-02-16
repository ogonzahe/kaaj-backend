package com.kaaj.api.controller;

import com.kaaj.api.dto.PanelResponse;
import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioRepository usuarioRepo;
    private final SaldoRepository saldoRepo;
    private final NotificacionRepository notiRepo;
    private final ReservaRepository reservaRepo;
    private final CondominioRepository condominioRepo;
    private final PasswordEncoder passwordEncoder;

    // ========== LOGIN ==========

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String correo = credentials.get("correo");
            String contrasena = credentials.get("contrasena");

            if (correo == null || contrasena == null ||
                    correo.trim().isEmpty() || contrasena.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Correo y contraseña son requeridos"));
            }

            correo = correo.trim().toLowerCase();
            Usuario usuario = usuarioRepo.findByCorreo(correo);

            if (usuario == null || !passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Correo o contraseña incorrectos"));
            }

            String rolNombre = usuario.getRolNombre();
            if (rolNombre == null || rolNombre.trim().isEmpty()) {
                switch (usuario.getRolId()) {
                    case 4:
                        rolNombre = "COPO";
                        break;
                    case 1:
                        rolNombre = "admin_usuario";
                        break;
                    case 2:
                        rolNombre = "USUARIO";
                        break;
                    case 3:
                        rolNombre = "SEGURIDAD";
                        break;
                    default:
                        rolNombre = "USUARIO";
                }
            }

            if ("COPO".equalsIgnoreCase(rolNombre) && usuario.getCondominio() != null) {
                usuario.setCondominio(null);
            }

            String redirectTo;
            switch (rolNombre.toUpperCase()) {
                case "COPO":
                    redirectTo = "/panel-copo";
                    break;
                case "ADMIN_USUARIO":
                    redirectTo = "/admin-condominio";
                    break;
                case "USUARIO":
                    redirectTo = "/residente";
                    break;
                case "SEGURIDAD":
                    redirectTo = "/seguridad";
                    break;
                default:
                    redirectTo = "/panel";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("id", usuario.getId());
            response.put("nombre", usuario.getNombre());
            response.put("correo", usuario.getCorreo());
            response.put("rolId", usuario.getRolId());
            response.put("rolNombre", rolNombre);
            response.put("telefono", usuario.getTelefono());
            response.put("activo", usuario.getActivo());
            response.put("redirectTo", redirectTo);

            if (usuario.getCondominio() != null && usuario.getCondominio().getId() != null) {
                response.put("condominioId", usuario.getCondominio().getId());
                response.put("condominioNombre", usuario.getCondominio().getNombre());
            }

            if (usuario.getApartamento() != null && usuario.getApartamento().getId() != null) {
                response.put("apartamentoId", usuario.getApartamento().getId());
                response.put("apartamentoNumero", usuario.getApartamento().getNumero());
            } else if (usuario.getNumeroCasa() != null) {
                response.put("numeroCasa", usuario.getNumeroCasa());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    // ========== ENDPOINTS DE USUARIOS ==========

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        try {
            List<Usuario> usuarios = usuarioRepo.findAll();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            log.error("Error obteniendo usuarios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> createUsuario(@RequestBody Map<String, Object> usuarioData) {
        try {
            String correo = (String) usuarioData.get("correo");
            if (correo == null || correo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo es requerido"));
            }

            correo = correo.trim().toLowerCase();
            if (usuarioRepo.existsByCorreo(correo)) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado"));
            }

            String contrasena = (String) usuarioData.get("contrasena");
            if (contrasena == null || contrasena.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña es requerida"));
            }

            Usuario usuario = new Usuario();
            usuario.setNombre((String) usuarioData.get("nombre"));
            usuario.setCorreo(correo);
            usuario.setContrasena(passwordEncoder.encode(contrasena));
            usuario.setTelefono((String) usuarioData.get("telefono"));
            usuario.setActivo(true);

            Integer rolId = (Integer) usuarioData.get("rolId");
            String rolNombre = (String) usuarioData.get("rol_nombre");

            if (rolId != null) {
                usuario.setRolId(rolId);
            } else {
                usuario.setRolId(1);
            }

            if (rolNombre != null) {
                usuario.setRolNombre(rolNombre);
            } else {
                usuario.setRolNombre("admin_usuario");
            }

            Integer condominioId = (Integer) usuarioData.get("condominioId");
            if (condominioId != null) {
                Optional<Condominio> condominioOpt = condominioRepo.findById(condominioId);
                condominioOpt.ifPresent(usuario::setCondominio);
            }

            Usuario saved = usuarioRepo.save(usuario);
            log.info("Usuario creado: {} | Rol: {}", saved.getNombre(), saved.getRolNombre());

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            log.error("Error creando usuario", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno al crear usuario"));
        }
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioRepo.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/usuarios/crear-usuario-condominio")
    public ResponseEntity<?> crearUsuarioCondominio(@RequestBody Map<String, Object> usuarioData) {
        try {
            if (usuarioData.get("condominioId") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El condominio es obligatorio"));
            }

            String correo = (String) usuarioData.get("correo");
            if (correo == null || correo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo es requerido"));
            }

            if (usuarioRepo.existsByCorreo(correo)) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado"));
            }

            Integer condominioId;
            try {
                condominioId = Integer.parseInt(usuarioData.get("condominioId").toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID de condominio inválido"));
            }

            Optional<Condominio> condominioOpt = condominioRepo.findById(condominioId);
            if (condominioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El condominio no existe"));
            }

            Condominio condominio = condominioOpt.get();

            String numeroCasaStr = "";
            if (usuarioData.containsKey("numero_casa") && usuarioData.get("numero_casa") != null) {
                numeroCasaStr = usuarioData.get("numero_casa").toString().trim();

                try {
                    int numeroCasa = Integer.parseInt(numeroCasaStr);
                    int maxCasas = condominio.getNumeroCasas() != null ? condominio.getNumeroCasas() : 9999;

                    if (numeroCasa < 1) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "El número de casa no puede ser menor a 1"));
                    }

                    if (numeroCasa > maxCasas) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "El número ingresado (" + numeroCasa
                                        + ") excede el límite de unidades del condominio (" + maxCasas + ")"));
                    }

                } catch (NumberFormatException e) {
                    // Allow alphanumeric house numbers like "101-B"
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El número de casa/departamento es obligatorio"));
            }

            String contrasena = (String) usuarioData.get("contrasena");
            if (contrasena == null || contrasena.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña es requerida"));
            }

            Usuario usuario = new Usuario();
            usuario.setNombre((String) usuarioData.get("nombre"));
            usuario.setCorreo(correo);
            usuario.setContrasena(passwordEncoder.encode(contrasena));
            usuario.setTelefono((String) usuarioData.get("telefono"));
            usuario.setCondominio(condominio);
            usuario.setNumeroCasa(numeroCasaStr);

            String rolNombre = (String) usuarioData.get("rol_nombre");
            if (rolNombre == null)
                rolNombre = "USUARIO";

            usuario.setRolNombre(rolNombre);

            if ("SEGURIDAD".equalsIgnoreCase(rolNombre)) {
                usuario.setRolId(3);
            } else {
                usuario.setRolId(2);
            }

            usuario.setActivo(true);

            Usuario saved = usuarioRepo.save(usuario);
            log.info("Usuario creado: {} | Casa: {}", saved.getNombre(), saved.getNumeroCasa());

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            log.error("Error creando usuario para condominio", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno al crear usuario"));
        }
    }

    @PostMapping("/usuarios/crear-admin-condominio")
    public ResponseEntity<?> crearAdminCondominio(@RequestBody Map<String, Object> usuarioData) {
        try {
            if (usuarioData.get("condominioId") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El condominio es obligatorio"));
            }

            String correo = (String) usuarioData.get("correo");
            if (usuarioRepo.existsByCorreo(correo)) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado"));
            }

            Integer condominioId = Integer.parseInt(usuarioData.get("condominioId").toString());
            Optional<Condominio> condominioOpt = condominioRepo.findById(condominioId);

            if (condominioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El condominio no existe"));
            }

            String contrasena = (String) usuarioData.get("contrasena");
            if (contrasena == null || contrasena.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña es requerida"));
            }

            Usuario usuario = new Usuario();
            usuario.setNombre((String) usuarioData.get("nombre"));
            usuario.setCorreo(correo);
            usuario.setContrasena(passwordEncoder.encode(contrasena));
            usuario.setTelefono((String) usuarioData.get("telefono"));
            usuario.setCondominio(condominioOpt.get());
            usuario.setRolId(1);
            usuario.setRolNombre("admin_usuario");
            usuario.setActivo(true);

            Usuario saved = usuarioRepo.save(usuario);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            log.error("Error creando admin de condominio", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al crear administrador"));
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioDetails) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepo.findById(id);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioOpt.get();

            if ("COPO".equalsIgnoreCase(usuario.getRolNombre()) && usuarioDetails.getCondominio() != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "COPO no puede tener condominio asignado"));
            }

            usuario.setNombre(usuarioDetails.getNombre());
            usuario.setCorreo(usuarioDetails.getCorreo());
            usuario.setTelefono(usuarioDetails.getTelefono());
            usuario.setCondominio(usuarioDetails.getCondominio());
            usuario.setActivo(usuarioDetails.getActivo());

            if (usuarioDetails.getNumeroCasa() != null) {
                usuario.setNumeroCasa(usuarioDetails.getNumeroCasa());
            }

            if (usuarioDetails.getContrasena() != null && !usuarioDetails.getContrasena().isEmpty()) {
                usuario.setContrasena(passwordEncoder.encode(usuarioDetails.getContrasena()));
            }

            Usuario updated = usuarioRepo.save(usuario);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            log.error("Error actualizando usuario {}", id, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al actualizar usuario"));
        }
    }

    // ========== ELIMINAR USUARIO ==========

    @DeleteMapping("/usuarios/{id}")
    @Transactional
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Usuario-Id", required = false) Integer usuarioId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        try {
            if (usuarioId == null || userRole == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Se requieren los headers X-Usuario-Id y X-User-Role"));
            }

            Optional<Usuario> usuarioAutenticadoOpt = usuarioRepo.findById(usuarioId);
            if (usuarioAutenticadoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            Usuario usuarioAutenticado = usuarioAutenticadoOpt.get();
            String rolAutenticado = usuarioAutenticado.getRolNombre();

            boolean tienePermisos = "admin_usuario".equalsIgnoreCase(rolAutenticado) ||
                                   "COPO".equalsIgnoreCase(rolAutenticado);

            if (!tienePermisos) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permisos para eliminar usuarios"));
            }

            Optional<Usuario> usuarioEliminarOpt = usuarioRepo.findById(id);
            if (usuarioEliminarOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            Usuario usuarioEliminar = usuarioEliminarOpt.get();

            if ("admin_usuario".equalsIgnoreCase(rolAutenticado)) {
                Integer condominioAdmin = usuarioAutenticado.getCondominioId();
                Integer condominioUsuario = usuarioEliminar.getCondominioId();

                if (condominioAdmin == null || !condominioAdmin.equals(condominioUsuario)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Solo puedes eliminar usuarios de tu propio condominio"));
                }
            }

            if (usuarioAutenticado.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No puedes eliminarte a ti mismo"));
            }

            // Delete associated data
            notiRepo.deleteByUsuarioId(id);
            saldoRepo.deleteByUsuarioId(id);
            reservaRepo.deleteByUsuarioId(id);
            usuarioRepo.deleteById(id);

            log.info("Usuario {} eliminado por usuario {}", id, usuarioId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Usuario eliminado exitosamente",
                "deletedId", id
            ));

        } catch (Exception e) {
            log.error("Error eliminando usuario {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar usuario"));
        }
    }

    // ========== RESTANTE DEL CONTROLADOR ==========

    @GetMapping("/admin/estadisticas")
    public ResponseEntity<?> getEstadisticasAdmin() {
        try {
            Map<String, Object> estadisticas = new HashMap<>();

            long totalCondominios = condominioRepo.count();
            long condominiosActivos = condominioRepo.findAll().stream()
                    .filter(c -> c.getActivo() != null && c.getActivo()).count();

            List<Usuario> todosUsuarios = usuarioRepo.findAll();
            long totalUsuarios = todosUsuarios.size();
            long administradores = todosUsuarios.stream()
                    .filter(u -> "admin_usuario".equalsIgnoreCase(u.getRolNombre())).count();
            long residentes = todosUsuarios.stream().filter(u -> "USUARIO".equalsIgnoreCase(u.getRolNombre())).count();
            long copos = todosUsuarios.stream().filter(u -> "COPO".equalsIgnoreCase(u.getRolNombre())).count();
            long seguridad = todosUsuarios.stream().filter(u -> "SEGURIDAD".equalsIgnoreCase(u.getRolNombre())).count();

            estadisticas.put("totalCondominios", totalCondominios);
            estadisticas.put("condominiosActivos", condominiosActivos);
            estadisticas.put("totalUsuarios", totalUsuarios);
            estadisticas.put("administradores", administradores);
            estadisticas.put("residentes", residentes);
            estadisticas.put("copropietarios", copos);
            estadisticas.put("seguridad", seguridad);
            estadisticas.put("fechaConsulta", new java.util.Date());

            return ResponseEntity.ok(estadisticas);

        } catch (Exception e) {
            log.error("Error obteniendo estadísticas admin", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al obtener estadísticas"));
        }
    }

    @GetMapping("/usuarios/{id}/condominios")
    public ResponseEntity<?> getCondominiosPorUsuario(@PathVariable Integer id) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepo.findById(id);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioOpt.get();
            String rolNombre = usuario.getRolNombre();

            if ("COPO".equalsIgnoreCase(rolNombre)) {
                return ResponseEntity.ok(condominioRepo.findAll());
            }

            if (usuario.getCondominio() != null) {
                return ResponseEntity.ok(List.of(usuario.getCondominio()));
            }

            return ResponseEntity.ok(new ArrayList<>());

        } catch (Exception e) {
            log.error("Error obteniendo condominios del usuario {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/panel-data")
    public ResponseEntity<?> getPanelData(@RequestParam String correo) {
        try {
            Usuario usuario = usuarioRepo.findByCorreo(correo);
            if (usuario == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));

            PanelResponse response = new PanelResponse();
            response.setUsuario(usuario);
            response.setSaldos(saldoRepo.findByUsuarioId(usuario.getId()));
            response.setNotificaciones(notiRepo.findByUsuarioIdAndLeidoFalse(usuario.getId()));

            java.time.LocalDate hoy = java.time.LocalDate.now();
            List<Reserva> reservas = reservaRepo.findByUsuarioIdAndFechaGreaterThanEqual(
                    usuario.getId(), hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth());
            response.setReservas(reservas);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo panel data para {}", correo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/usuarios/admin")
    public ResponseEntity<List<Usuario>> getAdministradores() {
        try {
            return ResponseEntity.ok(usuarioRepo.findByRolNombre("admin_usuario"));
        } catch (Exception e) {
            log.error("Error obteniendo administradores", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/usuarios/admin")
    public ResponseEntity<?> crearAdmin(@RequestBody Usuario usuario) {
        try {
            if (usuarioRepo.existsByCorreo(usuario.getCorreo())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Correo ya registrado"));
            }
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            usuario.setRolId(1);
            usuario.setRolNombre("admin_usuario");
            usuario.setActivo(true);
            return ResponseEntity.ok(usuarioRepo.save(usuario));
        } catch (Exception e) {
            log.error("Error creando admin", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al crear administrador"));
        }
    }

    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestBody Map<String, Boolean> estado) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepo.findById(id);
            if (usuarioOpt.isEmpty())
                return ResponseEntity.notFound().build();
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(estado.get("activo"));
            usuarioRepo.save(usuario);
            return ResponseEntity.ok(Map.of("message", "Estado actualizado"));
        } catch (Exception e) {
            log.error("Error cambiando estado del usuario {}", id, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al cambiar estado"));
        }
    }

    @GetMapping("/usuarios/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuarios(@RequestParam(required = false) String query) {
        try {
            if (query != null && !query.trim().isEmpty()) {
                return ResponseEntity.ok(usuarioRepo.findByNombreContainingOrCorreoContaining(query));
            }
            return ResponseEntity.ok(usuarioRepo.findAll());
        } catch (Exception e) {
            log.error("Error buscando usuarios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuarios/por-condominio/{condominioId}")
    public ResponseEntity<List<Usuario>> getUsuariosPorCondominio(@PathVariable Integer condominioId) {
        try {
            return ResponseEntity.ok(usuarioRepo.findByCondominioId(condominioId));
        } catch (Exception e) {
            log.error("Error obteniendo usuarios del condominio {}", condominioId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuarios/por-rol/{rolNombre}")
    public ResponseEntity<List<Usuario>> getUsuariosPorRol(@PathVariable String rolNombre) {
        try {
            return ResponseEntity.ok(usuarioRepo.findByRolNombre(rolNombre));
        } catch (Exception e) {
            log.error("Error obteniendo usuarios por rol {}", rolNombre, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
