package com.kaaj.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaaj.api.model.Amenidad;
import com.kaaj.api.repository.AmenidadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/amenidades")
public class AmenidadController {

    private final AmenidadRepository amenidadRepo;

    // Herramienta para convertir la lista de días a texto JSON
    private final ObjectMapper mapper = new ObjectMapper();

    // === LISTAR (Con filtro opcional por condominio) ===
    @GetMapping
    public ResponseEntity<List<Amenidad>> getAllAmenidades(@RequestParam(required = false) Integer condominioId) {
        if (condominioId != null) {
            // Usa el método que agregamos al repositorio
            return ResponseEntity.ok(amenidadRepo.findByCondominioId(condominioId));
        }
        return ResponseEntity.ok(amenidadRepo.findAll());
    }

    // === CREAR ===
    @PostMapping
    public ResponseEntity<?> crearAmenidad(@RequestBody Map<String, Object> data) {
        try {
            Amenidad amenidad = new Amenidad();

            // 1. Datos básicos
            amenidad.setNombre((String) data.get("nombre"));
            amenidad.setDescripcion((String) data.get("descripcion"));

            // Convertir capacidad de forma segura (puede venir como número o string)
            Object cap = data.get("capacidad");
            amenidad.setCapacidad(cap != null ? Integer.parseInt(cap.toString()) : 1);

            // 2. Estado (Convertir String a Enum)
            String estadoStr = (String) data.get("estado");
            if (estadoStr != null) {
                try {
                    amenidad.setEstado(Amenidad.EstadoAmenidad.valueOf(estadoStr));
                } catch (IllegalArgumentException e) {
                    amenidad.setEstado(Amenidad.EstadoAmenidad.activa);
                }
            }

            // 3. Horarios (Convertir String "08:00" a Time SQL)
            String horaA = (String) data.get("hora_apertura");
            String horaC = (String) data.get("hora_cierre");

            // Agregar segundos (:00) si el formato es corto (HH:mm)
            if (horaA != null && horaA.length() == 5) horaA += ":00";
            if (horaC != null && horaC.length() == 5) horaC += ":00";

            if (horaA != null) amenidad.setHoraApertura(Time.valueOf(horaA));
            if (horaC != null) amenidad.setHoraCierre(Time.valueOf(horaC));

            // 4. Días Disponibles (Convertir Lista a JSON String)
            Object diasObj = data.get("dias_disponibles");
            if (diasObj != null) {
                // Convierte ["lunes", "martes"] a '["lunes", "martes"]'
                String jsonDias = mapper.writeValueAsString(diasObj);
                amenidad.setDiasDisponibles(jsonDias);
            }

            // 5. Asignar ID Condominio
            if (data.get("condominio_id") != null) {
                Integer condominioId = Integer.parseInt(data.get("condominio_id").toString());
                amenidad.setCondominioId(condominioId);
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "El condominio es obligatorio"));
            }

            Amenidad guardada = amenidadRepo.save(amenidad);
            return ResponseEntity.ok(guardada);

        } catch (Exception e) {
            log.error("Error al guardar amenidad", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error al guardar amenidad"));
        }
    }

    // === ACTUALIZAR ===
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAmenidad(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        try {
            Optional<Amenidad> opt = amenidadRepo.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            Amenidad amenidad = opt.get();

            if (data.containsKey("nombre")) amenidad.setNombre((String) data.get("nombre"));
            if (data.containsKey("descripcion")) amenidad.setDescripcion((String) data.get("descripcion"));

            if (data.containsKey("capacidad")) {
                amenidad.setCapacidad(Integer.parseInt(data.get("capacidad").toString()));
            }

            if (data.containsKey("estado")) {
                String estadoStr = (String) data.get("estado");
                try {
                    amenidad.setEstado(Amenidad.EstadoAmenidad.valueOf(estadoStr));
                } catch (Exception e) { /* ignorar error */ }
            }

            if (data.containsKey("hora_apertura")) {
                String hora = (String) data.get("hora_apertura");
                if (hora.length() == 5) hora += ":00";
                amenidad.setHoraApertura(Time.valueOf(hora));
            }

            if (data.containsKey("hora_cierre")) {
                String hora = (String) data.get("hora_cierre");
                if (hora.length() == 5) hora += ":00";
                amenidad.setHoraCierre(Time.valueOf(hora));
            }

            if (data.containsKey("dias_disponibles")) {
                String jsonDias = mapper.writeValueAsString(data.get("dias_disponibles"));
                amenidad.setDiasDisponibles(jsonDias);
            }

            return ResponseEntity.ok(amenidadRepo.save(amenidad));

        } catch (Exception e) {
            log.error("Error al actualizar amenidad con id: {}", id, e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error al actualizar amenidad"));
        }
    }

    // === ELIMINAR ===
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAmenidad(@PathVariable Integer id) {
        if (!amenidadRepo.existsById(id)) return ResponseEntity.notFound().build();
        amenidadRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Amenidad eliminada"));
    }
}