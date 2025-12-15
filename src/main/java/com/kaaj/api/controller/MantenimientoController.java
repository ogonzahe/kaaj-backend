package com.kaaj.api.controller;

import java.util.List;

import com.kaaj.api.model.*;
import com.kaaj.api.service.MantenimientoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kaaj.api.dto.MantenimientoDTO;
import com.kaaj.api.dto.ReporteMantenimientoDTO;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/mantenimiento")
public class MantenimientoController {

@Autowired
 private MantenimientoService service;
 @PostMapping("/crear")
    public ResponseEntity<MantenimientoEntity> crearReporteMantenimiento(@RequestBody ReporteMantenimientoDTO reporteDTO) {

        try {
            MantenimientoEntity nuevoMantenimiento = service.crearMantenimiento(reporteDTO);
            return new ResponseEntity<>(nuevoMantenimiento, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/llenar")
    public ResponseEntity<List<MantenimientoDTO>> obtenerHistorialReportes() {
        List<MantenimientoDTO> historial = service.obtenerHistorialReportes();
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }

    @PutMapping("/atendido/{id}") 
    public ResponseEntity<MantenimientoEntity> marcarComoAtendido(@PathVariable Integer id) {
        try {
            MantenimientoEntity mantenimientoAtendido = service.actualizarEstatusAResuelto(id);
            return new ResponseEntity<>(mantenimientoAtendido, HttpStatus.OK);
            
        } catch (Exception e) {
            if (e.getMessage().contains("Reporte de Mantenimiento no encontrado")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }

}