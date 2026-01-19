package com.kaaj.api.controller;

import com.kaaj.api.dto.PanelResponse;
import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private SaldoRepository saldoRepo;

    @Autowired
    private NotificacionRepository notiRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private CondominioRepository condominioRepo;

    @Autowired
    private EntityManager entityManager;

    // ========== LOGIN ==========

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            System.out.println("========== LOGIN INICIADO ==========");

            String correo = credentials.get("correo");
            String contrasena = credentials.get("contrasena");

            if (correo == null || contrasena == null ||
                    correo.trim().isEmpty() || contrasena.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Correo y contraseña son requeridos"));
            }

            correo = correo.trim().toLowerCase();
            Usuario usuario = null;

            try {
                usuario = usuarioRepo.findByCorreoAndContrasena(correo, contrasena);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (usuario == null) {
                try {
                    Query query = entityManager.createNativeQuery(
                            "SELECT * FROM usuarios WHERE correo = ?1 AND contrasena = ?2",
                            Usuario.class);
                    query.setParameter(1, correo);
                    query.setParameter(2, contrasena);

                    List<Usuario> resultados = query.getResultList();
                    if (!resultados.isEmpty()) {
                        usuario = resultados.get(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (usuario == null) {
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

            // COPO: NO debe tener condominio
            if ("COPO".equalsIgnoreCase(rolNombre) && usuario.getCondominio() != null) {
                usuario.setCondominio(null);
            }

            String redirectTo = "/";
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

            // Aquí intenta devolver el apartamento si existe la relación antigua
            if (usuario.getApartamento() != null && usuario.getApartamento().getId() != null) {
                response.put("apartamentoId", usuario.getApartamento().getId());
                response.put("apartamentoNumero", usuario.getApartamento().getNumero());
            } else if (usuario.getNumeroCasa() != null) {
                // Si usamos el nuevo campo directo
                response.put("numeroCasa", usuario.getNumeroCasa());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== CREAR USUARIO GENERAL (PARA PANEL COPO) ==========
    @PostMapping("/usuarios")
    public ResponseEntity<?> createUsuario(@RequestBody Map<String, Object> usuarioData) {
        try {
            System.out.println("🔍 Creando usuario (endpoint general): " + usuarioData);

            // 1. Validaciones básicas
            String correo = (String) usuarioData.get("correo");
            if (correo == null || correo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo es requerido"));
            }

            correo = correo.trim().toLowerCase();
            if (usuarioRepo.existsByCorreo(correo)) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado"));
            }

            // 2. Validar contraseña
            String contrasena = (String) usuarioData.get("contrasena");
            if (contrasena == null || contrasena.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña es requerida"));
            }

            // 3. Configurar Usuario
            Usuario usuario = new Usuario();
            usuario.setNombre((String) usuarioData.get("nombre"));
            usuario.setCorreo(correo);
            usuario.setContrasena(contrasena);
            usuario.setTelefono((String) usuarioData.get("telefono"));
            usuario.setActivo(true);

            // 4. Asignar Rol
            Integer rolId = (Integer) usuarioData.get("rolId");
            String rolNombre = (String) usuarioData.get("rol_nombre");

            if (rolId != null) {
                usuario.setRolId(rolId);
            } else {
                // Default a admin_usuario si no se especifica
                usuario.setRolId(1);
            }

            if (rolNombre != null) {
                usuario.setRolNombre(rolNombre);
            } else {
                usuario.setRolNombre("admin_usuario");
            }

            // 5. Asignar Condominio principal si viene
            Integer condominioId = (Integer) usuarioData.get("condominioId");
            if (condominioId != null) {
                Optional<Condominio> condominioOpt = condominioRepo.findById(condominioId);
                condominioOpt.ifPresent(usuario::setCondominio);
            }

            // 6. Guardar Usuario
            Usuario saved = usuarioRepo.save(usuario);
            System.out.println("✅ Usuario creado: " + saved.getNombre() +
                    " | Rol: " + saved.getRolNombre() +
                    " | Condominio: " + (saved.getCondominio() != null ? saved.getCondominio().getId() : "ninguno"));

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            System.err.println("❌ Error creando usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno al crear usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioRepo.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== CREAR USUARIO PARA UN CONDOMINIO (CORREGIDO CON VALIDACIÓN DE CASA) ==========

    @PostMapping("/usuarios/crear-usuario-condominio")
    public ResponseEntity<?> crearUsuarioCondominio(@RequestBody Map<String, Object> usuarioData) {
        try {
            System.out.println("🔍 Creando usuario para condominio: " + usuarioData);

            // 1. Validaciones básicas
            if (usuarioData.get("condominioId") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El condominio es obligatorio"));
            }

            String correo = (String) usuarioData.get("correo");
            if (usuarioRepo.existsByCorreo(correo)) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado"));
            }

            // 2. Obtener Condominio
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

            // 3. VALIDACIÓN DE NÚMERO DE CASA / APARTAMENTO
            String numeroCasaStr = "";
            if (usuarioData.containsKey("numero_casa") && usuarioData.get("numero_casa") != null) {
                numeroCasaStr = usuarioData.get("numero_casa").toString().trim();

                // Validar rango numérico si es posible
                try {
                    int numeroCasa = Integer.parseInt(numeroCasaStr);
                    // Si el condominio tiene definido un número de casas, validar el límite
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
                    // Si permites letras (ej: "101-B"), puedes ignorar este catch o validar formato específico
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El número de casa/departamento es obligatorio"));
            }

            // 4. Configurar Usuario
            Usuario usuario = new Usuario();
            usuario.setNombre((String) usuarioData.get("nombre"));
            usuario.setCorreo(correo);
            usuario.setContrasena((String) usuarioData.get("contrasena"));
            usuario.setTelefono((String) usuarioData.get("telefono"));
            usuario.setCondominio(condominio);

            // GUARDAR EL NÚMERO DE CASA
            usuario.setNumeroCasa(numeroCasaStr);

            // Asignar Rol
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
            System.out.println("✅ Usuario creado: " + saved.getNombre() + " | Casa: " + saved.getNumeroCasa());

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            System.err.println("❌ Error creando usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno al crear usuario: " + e.getMessage()));
        }
    }

    // ========== CREAR ADMINISTRADOR (PARA COPO) ==========

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

            Usuario usuario = new Usuario();
            usuario.setNombre((String) usuarioData.get("nombre"));
            usuario.setCorreo(correo);
            usuario.setContrasena((String) usuarioData.get("contrasena"));
            usuario.setTelefono((String) usuarioData.get("telefono"));
            usuario.setCondominio(condominioOpt.get());
            usuario.setRolId(1);
            usuario.setRolNombre("admin_usuario");
            usuario.setActivo(true);

            Usuario saved = usuarioRepo.save(usuario);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
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

            // Actualizar número de casa si viene en la edición
            if (usuarioDetails.getNumeroCasa() != null) {
                usuario.setNumeroCasa(usuarioDetails.getNumeroCasa());
            }

            if (usuarioDetails.getContrasena() != null && !usuarioDetails.getContrasena().isEmpty()) {
                usuario.setContrasena(usuarioDetails.getContrasena());
            }

            Usuario updated = usuarioRepo.save(usuario);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al actualizar usuario"));
        }
    }

    // ========== MÉTODO PRIVADO PARA ELIMINACIÓN SIMPLE (CORREGIDO CON TRANSACCIÓN) ==========

    @Transactional
    private ResponseEntity<?> deleteUsuarioSimple(Integer id) {
        try {
            System.out.println("🗑️ ========== INICIANDO ELIMINACIÓN SIMPLE ==========");
            System.out.println("🔍 Usuario ID: " + id);

            // Verificar existencia del usuario
            if (!usuarioRepo.existsById(id)) {
                System.out.println("❌ Usuario ID " + id + " no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            // Verificar datos asociados (solo para logging)
            List<Notificacion> notificaciones = notiRepo.findByUsuarioId(id);
            List<Saldo> saldos = saldoRepo.findByUsuarioId(id);
            List<Reserva> reservas = reservaRepo.findByUsuarioId(id);

            System.out.println("📊 Datos asociados encontrados:");
            System.out.println("   • Notificaciones: " + notificaciones.size());
            System.out.println("   • Saldos: " + saldos.size());
            System.out.println("   • Reservas: " + reservas.size());

            // 1. ELIMINAR NOTIFICACIONES PRIMERO (EVITA FK CONSTRAINT)
            System.out.println("🔧 Eliminando notificaciones...");
            if (!notificaciones.isEmpty()) {
                try {
                    notiRepo.deleteByUsuarioId(id);
                    System.out.println("   ✅ " + notificaciones.size() + " notificaciones eliminadas");
                } catch (Exception e) {
                    System.err.println("   ❌ Error eliminando notificaciones: " + e.getMessage());
                    // Continuar de todos modos
                }
            }

            // 2. ELIMINAR SALDOS
            System.out.println("💰 Eliminando saldos...");
            if (!saldos.isEmpty()) {
                try {
                    saldoRepo.deleteByUsuarioId(id);
                    System.out.println("   ✅ " + saldos.size() + " saldos eliminados");
                } catch (Exception e) {
                    System.err.println("   ❌ Error eliminando saldos: " + e.getMessage());
                    // Continuar de todos modos
                }
            }

            // 3. ELIMINAR RESERVAS
            System.out.println("📅 Eliminando reservas...");
            if (!reservas.isEmpty()) {
                try {
                    reservaRepo.deleteByUsuarioId(id);
                    System.out.println("   ✅ " + reservas.size() + " reservas eliminadas");
                } catch (Exception e) {
                    System.err.println("   ❌ Error eliminando reservas: " + e.getMessage());
                    // Continuar de todos modos
                }
            }

            // 4. ELIMINAR EL USUARIO (AHORA DEBERÍA FUNCIONAR)
            System.out.println("👤 Eliminando usuario...");
            try {
                usuarioRepo.deleteById(id);
                System.out.println("   ✅ Usuario ID " + id + " eliminado exitosamente");
            } catch (Exception e) {
                System.err.println("   ❌ Error eliminando usuario: " + e.getMessage());
                throw new RuntimeException("No se pudo eliminar el usuario después de limpiar datos: " + e.getMessage());
            }

            System.out.println("🎉 ELIMINACIÓN COMPLETADA EXITOSAMENTE");

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Usuario eliminado exitosamente con todos sus datos asociados",
                "deletedId", id,
                "details", Map.of(
                    "notificacionesEliminadas", notificaciones.size(),
                    "saldosEliminados", saldos.size(),
                    "reservasEliminadas", reservas.size()
                )
            ));

        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO EN ELIMINACIÓN: " + e.getMessage());
            e.printStackTrace();

            // La transacción se revertirá automáticamente gracias a @Transactional
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al eliminar usuario: " + e.getMessage(),
                        "suggestion", "Verifique que no haya otras relaciones en la base de datos",
                        "code", "DELETE_FAILED"
                    ));
        }
    }

    // ========== ELIMINAR USUARIO PRINCIPAL (CORREGIDO) ==========

    @DeleteMapping("/usuarios/{id}")
    @Transactional
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Usuario-Id", required = false) Integer usuarioId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        try {
            System.out.println("🚀 ========== INICIANDO ELIMINACIÓN DE USUARIO ==========");
            System.out.println("🔍 Detalles de solicitud:");
            System.out.println("   • Usuario a eliminar ID: " + id);
            System.out.println("   • Usuario autenticado ID: " + usuarioId);
            System.out.println("   • Rol autenticado: " + userRole);

            // Si no hay headers de autenticación, usar método simple
            if (usuarioId == null || userRole == null) {
                System.out.println("⚠️ Headers no recibidos, usando eliminación simple");
                return deleteUsuarioSimple(id);
            }

            // 1. VERIFICAR USUARIO AUTENTICADO
            Optional<Usuario> usuarioAutenticadoOpt = usuarioRepo.findById(usuarioId);
            if (usuarioAutenticadoOpt.isEmpty()) {
                System.out.println("❌ Usuario autenticado no encontrado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            Usuario usuarioAutenticado = usuarioAutenticadoOpt.get();
            String rolAutenticado = usuarioAutenticado.getRolNombre();
            System.out.println("   • Usuario autenticado: " + usuarioAutenticado.getNombre());
            System.out.println("   • Rol autenticado: " + rolAutenticado);

            // 2. VERIFICAR PERMISOS
            boolean tienePermisos = "admin_usuario".equalsIgnoreCase(rolAutenticado) ||
                                   "COPO".equalsIgnoreCase(rolAutenticado);

            if (!tienePermisos) {
                System.out.println("❌ Usuario no tiene permisos para eliminar");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permisos para eliminar usuarios"));
            }

            // 3. VERIFICAR USUARIO A ELIMINAR
            Optional<Usuario> usuarioEliminarOpt = usuarioRepo.findById(id);
            if (usuarioEliminarOpt.isEmpty()) {
                System.out.println("❌ Usuario a eliminar no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            Usuario usuarioEliminar = usuarioEliminarOpt.get();
            System.out.println("   • Usuario a eliminar: " + usuarioEliminar.getNombre());
            System.out.println("   • Rol: " + usuarioEliminar.getRolNombre());

            // 4. VALIDACIONES ESPECÍFICAS POR ROL
            if ("admin_usuario".equalsIgnoreCase(rolAutenticado)) {
                Integer condominioAdmin = usuarioAutenticado.getCondominioId();
                Integer condominioUsuario = usuarioEliminar.getCondominioId();

                System.out.println("   • Condominio admin: " + condominioAdmin);
                System.out.println("   • Condominio usuario: " + condominioUsuario);

                if (condominioAdmin == null || !condominioAdmin.equals(condominioUsuario)) {
                    System.out.println("❌ Admin intenta eliminar usuario de otro condominio");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Solo puedes eliminar usuarios de tu propio condominio"));
                }
            }

            // 5. EVITAR AUTO-ELIMINACIÓN
            if (usuarioAutenticado.getId().equals(id)) {
                System.out.println("❌ Intento de auto-eliminación");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No puedes eliminarte a ti mismo"));
            }

            // 6. REALIZAR ELIMINACIÓN (usando el método simple mejorado)
            System.out.println("✅ Permisos validados, procediendo con eliminación...");
            return deleteUsuarioSimple(id);

        } catch (Exception e) {
            System.err.println("❌ ERROR GENERAL EN ELIMINACIÓN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al eliminar usuario: " + e.getMessage()));
        }
    }

    // ========== ENDPOINT ALTERNATIVO PARA ELIMINAR ==========

    @DeleteMapping("/usuarios/eliminar/{id}")
    @Transactional
    public ResponseEntity<?> eliminarUsuarioAlternativo(
            @PathVariable Integer id,
            @RequestParam Integer adminId,
            @RequestParam String adminRol) {
        try {
            System.out.println("🔧 Usando endpoint alternativo para eliminar usuario ID: " + id);
            return deleteUsuario(id, adminId, adminRol);
        } catch (Exception e) {
            System.out.println("⚠️ Falló método principal, usando eliminación simple");
            return deleteUsuarioSimple(id);
        }
    }

    // ========== ENDPOINT SIMPLE PARA ELIMINAR (SIN VALIDACIONES) ==========

    @DeleteMapping("/usuarios/simple/{id}")
    @Transactional
    public ResponseEntity<?> eliminarUsuarioSimpleEndpoint(@PathVariable Integer id) {
        System.out.println("🎯 Endpoint simple de eliminación para usuario ID: " + id);
        return deleteUsuarioSimple(id);
    }

    // ========== MÉTODO FORCE DELETE (ELIMINACIÓN FORZADA) ==========

    @DeleteMapping("/usuarios/force/{id}")
    @Transactional
    public ResponseEntity<?> forceDeleteUsuario(@PathVariable Integer id) {
        try {
            System.out.println("💥 ========== ELIMINACIÓN FORZADA ==========");
            System.out.println("⚠️ ADVERTENCIA: Este método eliminará TODO sin validaciones");
            System.out.println("🔍 Usuario ID: " + id);

            // Verificar si el usuario existe
            if (!usuarioRepo.existsById(id)) {
                System.out.println("❌ Usuario no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            // 1. Intentar eliminar todo en orden específico
            System.out.println("🔄 PASO 1: Limpiando todas las relaciones...");

            // Eliminar notificaciones
            try {
                int notificacionesCount = notiRepo.findByUsuarioId(id).size();
                notiRepo.deleteByUsuarioId(id);
                System.out.println("   ✅ Notificaciones eliminadas: " + notificacionesCount);
            } catch (Exception e) {
                System.out.println("   ⚠️ No se pudieron eliminar notificaciones: " + e.getMessage());
            }

            // Eliminar saldos
            try {
                int saldosCount = saldoRepo.findByUsuarioId(id).size();
                saldoRepo.deleteByUsuarioId(id);
                System.out.println("   ✅ Saldos eliminados: " + saldosCount);
            } catch (Exception e) {
                System.out.println("   ⚠️ No se pudieron eliminar saldos: " + e.getMessage());
            }

            // Eliminar reservas
            try {
                int reservasCount = reservaRepo.findByUsuarioId(id).size();
                reservaRepo.deleteByUsuarioId(id);
                System.out.println("   ✅ Reservas eliminadas: " + reservasCount);
            } catch (Exception e) {
                System.out.println("   ⚠️ No se pudieron eliminar reservas: " + e.getMessage());
            }

            // 2. Eliminar usuario
            System.out.println("🔄 PASO 2: Eliminando usuario...");
            try {
                usuarioRepo.deleteById(id);
                System.out.println("   ✅ Usuario eliminado exitosamente");
            } catch (Exception e) {
                System.err.println("   ❌ Error eliminando usuario: " + e.getMessage());

                // Último intento: usar query nativa
                try {
                    System.out.println("   🔧 Intentando eliminación con query nativa...");
                    Query query = entityManager.createNativeQuery("DELETE FROM usuarios WHERE id = ?");
                    query.setParameter(1, id);
                    int deleted = query.executeUpdate();

                    if (deleted > 0) {
                        System.out.println("   ✅ Usuario eliminado con query nativa");
                    } else {
                        throw new RuntimeException("No se pudo eliminar con ningún método");
                    }
                } catch (Exception ex) {
                    throw new RuntimeException("Error crítico: " + ex.getMessage());
                }
            }

            System.out.println("🎉 ELIMINACIÓN FORZADA COMPLETADA");

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Eliminación forzada completada exitosamente",
                "warning", "Se eliminaron TODOS los datos asociados al usuario",
                "deletedId", id
            ));

        } catch (Exception e) {
            System.err.println("💀 ERROR EN ELIMINACIÓN FORZADA: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error crítico en eliminación forzada: " + e.getMessage(),
                        "suggestion", "Revise manualmente las relaciones en la base de datos",
                        "code", "FORCE_DELETE_FAILED"
                    ));
        }
    }

    // ========== RESTANTE DEL CONTROLADOR (SIN CAMBIOS) ==========

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
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/panel-data")
    public ResponseEntity<?> getPanelData(@RequestParam String correo) {
        try {
            Usuario usuario = usuarioRepo.findByCorreo(correo);
            if (usuario == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");

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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @GetMapping("/usuarios/admin")
    public ResponseEntity<List<Usuario>> getAdministradores() {
        try {
            return ResponseEntity.ok(usuarioRepo.findByRolNombre("admin_usuario"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/usuarios/admin")
    public ResponseEntity<?> crearAdmin(@RequestBody Usuario usuario) {
        try {
            if (usuarioRepo.existsByCorreo(usuario.getCorreo())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Correo ya registrado"));
            }
            usuario.setRolId(1);
            usuario.setRolNombre("admin_usuario");
            usuario.setActivo(true);
            return ResponseEntity.ok(usuarioRepo.save(usuario));
        } catch (Exception e) {
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
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuarios/por-condominio/{condominioId}")
    public ResponseEntity<List<Usuario>> getUsuariosPorCondominio(@PathVariable Integer condominioId) {
        try {
            return ResponseEntity.ok(usuarioRepo.findByCondominioId(condominioId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuarios/por-rol/{rolNombre}")
    public ResponseEntity<List<Usuario>> getUsuariosPorRol(@PathVariable String rolNombre) {
        try {
            return ResponseEntity.ok(usuarioRepo.findByRolNombre(rolNombre));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}