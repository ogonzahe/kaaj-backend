package com.kaaj.api.controller;

import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private AmenidadRepository amenidadRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    // Obtener reservas por mes y año
    @GetMapping
    public ResponseEntity<?> getReservasPorMes(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            System.out.println("=== SOLICITANDO RESERVAS PARA: " + month + "/" + year + " ===");

            List<Reserva> reservas = reservaRepo.findByMesAndAnio(month, year);
            System.out.println("Reservas encontradas: " + reservas.size());

            return ResponseEntity.ok(reservas);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    // Crear nueva reserva
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, Object> reservaData) {
        try {
            System.out.println("=== CREANDO NUEVA RESERVA ===");
            System.out.println("Datos recibidos: " + reservaData);

            String amenidadNombre = (String) reservaData.get("amenidad");
            Integer dia = (Integer) reservaData.get("dia");
            Integer mes = (Integer) reservaData.get("mes");
            Integer anio = (Integer) reservaData.get("anio");
            String hora = (String) reservaData.get("hora");
            Integer usuarioId = (Integer) reservaData.get("usuario_id");

            // Validaciones básicas
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
                            .body("Formato de hora inválido: " + hora);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Formato de hora inválido: " + hora);
            }

            // Validar que el usuario existe
            Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
            }

            // Validar que el usuario no tenga otra reserva el mismo día
            Reserva reservaExistente = reservaRepo.findByUsuarioAndDiaAndMesAndAnio(usuario, dia, mes, anio);
            if (reservaExistente != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya tienes una reserva para este día");
            }

            // Validar que la amenidad no esté reservada en ese horario
            Reserva conflictoAmenidad = reservaRepo.findByAmenidadAndDiaAndMesAndAnioAndHoraInicio(
                    amenidadNombre, dia, mes, anio, horaInicio);
            if (conflictoAmenidad != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La amenidad ya está reservada en este horario");
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
            System.out.println("ERROR creando reserva: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear reserva: " + e.getMessage());
        }
    }

    // Obtener amenidades activas
    @GetMapping("/amenidades")
    public ResponseEntity<?> getAmenidadesActivas() {
        try {
            System.out.println("=== SOLICITANDO AMENIDADES ACTIVAS ===");

            List<Amenidad> amenidades = amenidadRepo.findByEstadoOrderByNombreAsc(Amenidad.EstadoAmenidad.activa);

            // Para testing, retornar amenidades por defecto si no hay en BD
            if (amenidades == null || amenidades.isEmpty()) {
                System.out.println("No hay amenidades en BD, retornando por defecto");
                return ResponseEntity.ok(List.of(
                        crearAmenidadMock(1, "Asador"),
                        crearAmenidadMock(2, "Gimnasio")));
            }

            System.out.println("Amenidades encontradas: " + amenidades.size());
            return ResponseEntity.ok(amenidades);

        } catch (Exception e) {
            System.out.println("ERROR obteniendo amenidades: " + e.getMessage());
            e.printStackTrace();
            // Retornar amenidades por defecto en caso de error
            return ResponseEntity.ok(List.of(
                    crearAmenidadMock(1, "Asador"),
                    crearAmenidadMock(2, "Gimnasio")));
        }
    }

    private Map<String, Object> crearAmenidadMock(Integer id, String nombre) {
        Map<String, Object> amenidad = new HashMap<>();
        amenidad.put("id", id);
        amenidad.put("nombre", nombre);
        amenidad.put("estado", "activa");
        return amenidad;
    }
}