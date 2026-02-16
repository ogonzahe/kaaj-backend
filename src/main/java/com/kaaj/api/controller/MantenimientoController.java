package com.kaaj.api.controller;

import java.util.List;

import com.kaaj.api.model.*;
import com.kaaj.api.service.MantenimientoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kaaj.api.dto.MantenimientoDTO;
import com.kaaj.api.dto.ReporteMantenimientoDTO;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mantenimiento")
public class MantenimientoController {

    private final MantenimientoService service;

    @PostMapping("/crear")
    public ResponseEntity<MantenimientoEntity> crearReporteMantenimiento(@RequestBody ReporteMantenimientoDTO reporteDTO) {
        try {
            MantenimientoEntity nuevoMantenimiento = service.crearMantenimiento(reporteDTO);
            return new ResponseEntity<>(nuevoMantenimiento, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error al crear reporte de mantenimiento", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/llenar")
    public ResponseEntity<List<MantenimientoDTO>> obtenerHistorialReportes() {
        try {
            List<MantenimientoDTO> historial = service.obtenerHistorialReportes();
            return new ResponseEntity<>(historial, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al obtener historial de reportes", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<MantenimientoDTO>> obtenerReportesPorCondominio(@PathVariable Integer condominioId) {
        try {
            List<MantenimientoDTO> reportes = service.obtenerReportesPorCondominio(condominioId);
            return new ResponseEntity<>(reportes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al obtener reportes por condominio con id: {}", condominioId, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MantenimientoDTO>> obtenerReportesPorUsuario(@PathVariable Integer usuarioId) {
        try {
            List<MantenimientoDTO> reportes = service.obtenerReportesPorUsuario(usuarioId);
            return new ResponseEntity<>(reportes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al obtener reportes por usuario con id: {}", usuarioId, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/atendido/{id}")
    public ResponseEntity<MantenimientoEntity> marcarComoAtendido(@PathVariable Integer id) {
        try {
            MantenimientoEntity mantenimientoAtendido = service.actualizarEstatusAResuelto(id);
            return new ResponseEntity<>(mantenimientoAtendido, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al marcar como atendido el reporte con id: {}", id, e);
            if (e.getMessage() != null && e.getMessage().contains("Reporte de Mantenimiento no encontrado")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/reabrir/{id}")
    public ResponseEntity<MantenimientoEntity> reabrirReporte(@PathVariable Integer id) {
        try {
            MantenimientoEntity mantenimientoReabierto = service.reabrirReporte(id);
            return new ResponseEntity<>(mantenimientoReabierto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al reabrir reporte con id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cancelado/{id}")
    public ResponseEntity<MantenimientoEntity> cancelarReporte(@PathVariable Integer id) {
        try {
            MantenimientoEntity mantenimientoCancelado = service.cancelarReporte(id);
            return new ResponseEntity<>(mantenimientoCancelado, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al cancelar reporte con id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Integer id) {
        try {
            service.eliminarReporte(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error al eliminar reporte con id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}