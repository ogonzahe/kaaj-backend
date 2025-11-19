package com.kaaj.api;

import com.kaaj.api.dto.CrearAmenidadDTO;
import com.kaaj.model.Amenidad;
import com.kaaj.service.AmenidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenidades")
@CrossOrigin(origins = "*") // Esto permite que el Frontend se conecte sin errores de seguridad.
public class AmenidadController {

    @Autowired
    private AmenidadService amenidadService;

    // 1. GET: Para que la pantalla cargue la lista de botones (Futbol, Padel...)
    // URL: GET http://localhost:8080/api/v1/amenidades
    @GetMapping
    public ResponseEntity<List<Amenidad>> listarAmenidades() {
        return ResponseEntity.ok(amenidadService.listarTodas());
    }

    // 2. POST: Para que el Administrador agregue nuevas opciones al sistema
    // URL: POST http://localhost:8080/api/v1/amenidades
    @PostMapping
    public ResponseEntity<Amenidad> crearAmenidad(@RequestBody CrearAmenidadDTO dto) {
        Amenidad guardada = amenidadService.crearAmenidad(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
}