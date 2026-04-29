package com.kaaj.api.controller;

import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepo;
    private final AmenidadRepository amenidadRepo;
    private final UsuarioRepository usuarioRepo;

    // Obtener reservas por mes y a??o
    @GetMapping
    public ResponseEntity<?> getReservasPorMes(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            log.info("Solicitando reservas para: {}/{}", month, year);

            List<Reserva> reservas = reservaRepo.findByMesAndAnio(month, year);
            log.info("Reservas encontradas: {}", reservas.size());

            return ResponseEntity.ok(reservas);

        } catch (Exception e) {
            log.error("Error obteniendo reservas para {}/{}", month, year, e);
            return ResponseEntity.ok(List.of());
        }
    }

    // Crear nueva reserva
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, Object> reservaData) {
        try {
            log.info("Creando nueva reserva");
            log.debug("Datos recibidos: {}", reservaData);

            String amenidadNombre = (String) reservaData.get("amenidad");
            Integer dia = (Integer) reservaData.get("dia");
            Integer mes = (Integer) reservaData.get("mes");
            Integer anio = (Integer) reservaData.get("anio");
            String hora = (String) reservaData.get("hora");
            Integer usuarioId = (Integer) reservaData.get("usuario_id");

            // Validaciones b??sicas
            if (amenidadNombre == null || dia == null || mes == null || anio == null || hora == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Faltan datos requeridos para la reserva");
            }

            // Normalizar formato de hora
            LocalTime horaInicio;
            try {
                if (hora.length() == 5) {
                    // Formato HH:mm -> convertir a HH:mm:00
                    horaInicio = LocalTime.parse(hora + ":00");
                } else if (hora.length() == 8) {
                    // Formato HH:mm:ss
                    horaInicio = LocalTime.parse(hora);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Formato de hora inv??lido: " + hora);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Formato de hora inv??lido: " + hora);
            }

            // Validar que el usuario existe
            Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
            }

            // Validar que la amenidad existe y obtener su capacidad
            Amenidad amenidad = amenidadRepo.findByNombre(amenidadNombre);
            if (amenidad == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Amenidad no encontrada: " + amenidadNombre);
            }
            int capacidad = amenidad.getCapacidad() != null && amenidad.getCapacidad() > 0
                    ? amenidad.getCapacidad()
                    : 1;

            // Regla: un usuario solo puede tener una reserva por amenidad por dia
            // (puede tener reservas en distintas amenidades el mismo dia).
            Reserva reservaExistente = reservaRepo.findByUsuarioAndAmenidadAndDiaAndMesAndAnio(
                    usuario, amenidadNombre, dia, mes, anio);
            if (reservaExistente != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya tienes una reserva en esta amenidad para este día");
            }

            // El slot esta lleno solo cuando se alcanza la capacidad de la amenidad.
            long reservasEnSlot = reservaRepo.countByAmenidadAndDiaAndMesAndAnioAndHoraInicio(
                    amenidadNombre, dia, mes, anio, horaInicio);
            if (reservasEnSlot >= capacidad) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Esta amenidad ya está completa en este horario");
            }

            // Crear la reserva
            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setUsuario(usuario);
            nuevaReserva.setAmenidad(amenidadNombre);
            nuevaReserva.setDia(dia);
            nuevaReserva.setMes(mes);
            nuevaReserva.setAnio(anio);
            nuevaReserva.setHoraInicio(horaInicio);
            nuevaReserva.setHoraFin(horaInicio.plusHours(1));
            nuevaReserva.setFechaReserva(LocalDate.of(anio, mes, dia));
            nuevaReserva.setHoraReserva(horaInicio);
            nuevaReserva.setEstado("confirmada");

            Reserva reservaGuardada = reservaRepo.save(nuevaReserva);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reserva creada exitosamente");
            response.put("reserva", reservaGuardada);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creando reserva", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear reserva");
        }
    }

    // Cancelar (eliminar) una reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Usuario-Id", required = false) Integer usuarioId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        try {
            Reserva reserva = reservaRepo.findById(id).orElse(null);
            if (reserva == null) {
                Map<String, Object> notFound = new HashMap<>();
                notFound.put("success", false);
                notFound.put("message", "Reserva no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }

            // Validar permisos: dueño de la reserva, admin del condominio o COPO.
            boolean esDueno = usuarioId != null
                    && reserva.getUsuario() != null
                    && usuarioId.equals(reserva.getUsuario().getId());
            boolean esAdmin = "admin_usuario".equalsIgnoreCase(userRole)
                    || "COPO".equalsIgnoreCase(userRole);

            if (!esDueno && !esAdmin) {
                Map<String, Object> forbidden = new HashMap<>();
                forbidden.put("success", false);
                forbidden.put("message", "No tienes permisos para cancelar esta reserva");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(forbidden);
            }

            reservaRepo.delete(reserva);
            log.info("Reserva {} cancelada por usuario {}", id, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reserva cancelada exitosamente");
            response.put("deletedId", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error cancelando reserva {}", id, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cancelar la reserva");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Obtener amenidades activas
    @GetMapping("/amenidades")
    public ResponseEntity<?> getAmenidadesActivas() {
        try {
            log.info("Solicitando amenidades activas");

            List<Amenidad> amenidades = amenidadRepo.findByEstadoOrderByNombreAsc(Amenidad.EstadoAmenidad.activa);

            if (amenidades == null || amenidades.isEmpty()) {
                log.info("No hay amenidades activas en BD");
                return ResponseEntity.ok(List.of());
            }

            log.info("Amenidades encontradas: {}", amenidades.size());
            return ResponseEntity.ok(amenidades);

        } catch (Exception e) {
            log.error("Error obteniendo amenidades", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener amenidades");
        }
    }
}