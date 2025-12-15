package com.kaaj.api.controller;

import com.kaaj.api.dto.LoginRequest;
import com.kaaj.api.dto.PanelResponse;
import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
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
    private EntityManager entityManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepo.findByCorreoAndContrasena(request.getCorreo(), request.getContrasena());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        Saldo saldo = saldoRepo.findTopByUsuarioOrderByActualizadoEnDesc(usuario);
        List<Notificacion> notificaciones = notiRepo.findByUsuarioAndLeidaFalse(usuario);
        Reserva reserva = reservaRepo.findTopByUsuarioOrderByFechaReservaAsc(usuario);

        String rolUsuario = usuario.getRolNombre();

        if (rolUsuario == null || rolUsuario.isEmpty()) {
            if (usuario.getRolId() != null) {
                switch (usuario.getRolId()) {
                    case 1:
                        rolUsuario = "admin_usuario";
                        break;
                    case 2:
                        rolUsuario = "USUARIO";
                        break;
                    case 3:
                        rolUsuario = "SEGURIDAD";
                        break;
                    default:
                        rolUsuario = "USUARIO";
                }
            } else {
                rolUsuario = "USUARIO";
            }
        }

        PanelResponse response = new PanelResponse(
                saldo != null ? saldo.getMonto() : null,
                saldo != null ? saldo.getFechaLimite() : null,
                notificaciones.stream().map(Notificacion::getTitulo).toList(),
                reserva != null ? reserva.getAmenidad() : null,
                reserva != null ? reserva.getFechaReserva() : null,
                reserva != null ? reserva.getHoraReserva() : null,
                rolUsuario);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<?> getNotificaciones() {
        try {
            List<Notificacion> notificaciones = notiRepo.findAll();
            return ResponseEntity.ok(notificaciones);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @PutMapping("/notificaciones/{id}/leer")
    @Transactional
    public ResponseEntity<?> marcarComoLeida(@PathVariable Integer id) {
        try {
            System.out.println("=== INTENTANDO MARCAR NOTIFICACIÓN " + id + " COMO LEÍDA ===");

            String selectQuery = "SELECT id, titulo, leida FROM notificaciones WHERE id = :id";
            Query select = entityManager.createNativeQuery(selectQuery);
            select.setParameter("id", id);

            Object[] currentState = (Object[]) select.getSingleResult();
            System.out.println("Estado actual - ID: " + currentState[0] + ", Título: " + currentState[1] + ", Leída: "
                    + currentState[2]);

            String updateQuery = "UPDATE notificaciones SET leida = 1 WHERE id = :id";
            Query update = entityManager.createNativeQuery(updateQuery);
            update.setParameter("id", id);
            int updated = update.executeUpdate();

            System.out.println("Filas actualizadas: " + updated);

            Object[] newState = (Object[]) select.getSingleResult();
            System.out.println(
                    "Estado nuevo - ID: " + newState[0] + ", Título: " + newState[1] + ", Leída: " + newState[2]);

            if (updated > 0) {
                return ResponseEntity.ok("Notificación marcada como leída");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificación no encontrada");
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al marcar como leída: " + e.getMessage());
        }
    }

    @GetMapping("/notificaciones/{id}/estado")
    public ResponseEntity<?> getEstadoNotificacion(@PathVariable Integer id) {
        try {
            String queryStr = "SELECT id, titulo, leida FROM notificaciones WHERE id = :id";
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("id", id);

            Object[] result = (Object[]) query.getSingleResult();

            Map<String, Object> estado = new HashMap<>();
            estado.put("id", result[0]);
            estado.put("titulo", result[1]);
            estado.put("leida", result[2]);

            System.out.println("Consultando estado - ID: " + result[0] + ", Leída: " + result[2]);

            return ResponseEntity.ok(estado);

        } catch (Exception e) {
            System.out.println("Error consultando estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificación no encontrada");
        }
    }

    @PutMapping("/notificaciones/{id}/leer-jpa")
    @Transactional
    public ResponseEntity<?> marcarComoLeidaJPA(@PathVariable Integer id) {
        try {
            System.out.println("=== MÉTODO JPA - Marcando notificación " + id + " como leída ===");

            Notificacion notificacion = notiRepo.findById(id).orElse(null);

            if (notificacion != null) {
                System.out.println(
                        "Encontrada: " + notificacion.getTitulo() + ", Leída actual: " + notificacion.getLeida());
                notificacion.setLeida(true);
                Notificacion saved = notiRepo.save(notificacion);
                System.out.println("Guardada: " + saved.getTitulo() + ", Leída nueva: " + saved.getLeida());
                return ResponseEntity.ok("Notificación marcada como leída (JPA)");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificación no encontrada");
            }

        } catch (Exception e) {
            System.out.println("ERROR JPA: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al marcar como leída: " + e.getMessage());
        }
    }

    @PostMapping("/notificaciones")
    public ResponseEntity<?> crearNotificacion(@RequestBody Map<String, Object> body) {
        try {
            Notificacion n = new Notificacion();

            n.setTitulo((String) body.get("titulo"));
            n.setDescripcion((String) body.get("mensaje"));
            n.setPrioridad((String) body.get("prioridad"));

            // Para admin (broadcast): usuario_id = null
            n.setUsuario(null);

            Notificacion saved = notiRepo.save(n);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creando notificación: " + e.getMessage());
        }
    }

    @PutMapping("/notificaciones/{id}")
    public ResponseEntity<?> editarNotificacion(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Notificacion n = notiRepo.findById(id).orElse(null);
            if (n == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificación no encontrada");

            if (body.get("titulo") != null)
                n.setTitulo((String) body.get("titulo"));
            if (body.get("mensaje") != null)
                n.setDescripcion((String) body.get("mensaje")); // <- tu front usa "mensaje"
            if (body.get("prioridad") != null)
                n.setPrioridad((String) body.get("prioridad"));

            Notificacion saved = notiRepo.save(n);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error editando notificación: " + e.getMessage());
        }
    }

    @DeleteMapping("/notificaciones/{id}")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Integer id) {
        try {
            if (!notiRepo.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificación no encontrada");
            }
            notiRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error eliminando notificación: " + e.getMessage());
        }
    }
}
