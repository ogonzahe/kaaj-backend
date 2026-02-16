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

            // Validar que el usuario no tenga otra reserva el mismo d??a
            Reserva reservaExistente = reservaRepo.findByUsuarioAndDiaAndMesAndAnio(usuario, dia, mes, anio);
            if (reservaExistente != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya tienes una reserva para este d??a");
            }

            // Validar que la amenidad no est?? reservada en ese horario
            Reserva conflictoAmenidad = reservaRepo.findByAmenidadAndDiaAndMesAndAnioAndHoraInicio(
                    amenidadNombre, dia, mes, anio, horaInicio);
            if (conflictoAmenidad != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La amenidad ya est?? reservada en este horario");
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