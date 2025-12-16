package com.kaaj.api;

import com.kaaj.api.dto.CrearReporteDTO;
import com.kaaj.api.model.Reporte;
import com.kaaj.api.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping
    public ResponseEntity<Reporte> crearReporte(@RequestBody CrearReporteDTO dto) {
        Integer usuarioId = 1; // Placeholder (luego JWT)
        Reporte nuevo = reporteService.crearReporte(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/mis-reportes")
    public ResponseEntity<List<Reporte>> misReportes() {
        Integer usuarioId = 1; // Placeholder (luego JWT)
        return ResponseEntity.ok(
                reporteService.obtenerReportesPorUsuario(usuarioId));
    }
}
