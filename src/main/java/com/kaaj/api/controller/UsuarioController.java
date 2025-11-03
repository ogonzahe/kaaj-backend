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

@CrossOrigin(origins = "*")
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

        PanelResponse response = new PanelResponse(
                saldo != null ? saldo.getMonto() : null,
                saldo != null ? saldo.getFechaLimite() : null,
                notificaciones.stream().map(Notificacion::getTitulo).toList(),
                reserva != null ? reserva.getAmenidad() : null,
                reserva != null ? reserva.getFechaReserva() : null,
                reserva != null ? reserva.getHoraReserva() : null);

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

            // Verificar estado actual
            String selectQuery = "SELECT id, titulo, leida FROM notificaciones WHERE id = :id";
            Query select = entityManager.createNativeQuery(selectQuery);
            select.setParameter("id", id);

            Object[] currentState = (Object[]) select.getSingleResult();
            System.out.println("Estado actual - ID: " + currentState[0] + ", Título: " + currentState[1] + ", Leída: " + currentState[2]);

            // Actualizar a leída
            String updateQuery = "UPDATE notificaciones SET leida = 1 WHERE id = :id";
            Query update = entityManager.createNativeQuery(updateQuery);
            update.setParameter("id", id);
            int updated = update.executeUpdate();

            System.out.println("Filas actualizadas: " + updated);

            // Verificar estado después de la actualización
            Object[] newState = (Object[]) select.getSingleResult();
            System.out.println("Estado nuevo - ID: " + newState[0] + ", Título: " + newState[1] + ", Leída: " + newState[2]);

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

    // Endpoint adicional para probar con JPA
    @PutMapping("/notificaciones/{id}/leer-jpa")
    @Transactional
    public ResponseEntity<?> marcarComoLeidaJPA(@PathVariable Integer id) {
        try {
            System.out.println("=== MÉTODO JPA - Marcando notificación " + id + " como leída ===");

            Notificacion notificacion = notiRepo.findById(id).orElse(null);

            if (notificacion != null) {
                System.out.println("Encontrada: " + notificacion.getTitulo() + ", Leída actual: " + notificacion.getLeida());
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
}