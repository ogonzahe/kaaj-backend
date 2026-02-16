package com.kaaj.api.controller;

import com.kaaj.api.model.Apartamento;
import com.kaaj.api.service.ApartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apartamentos")
public class ApartamentoController {

    private final ApartamentoService apartamentoService;

    // Obtener apartamentos por condominio
    @GetMapping
    public ResponseEntity<?> getApartamentosByCondominio(@RequestParam Integer condominioId) {
        try {
            List<Apartamento> apartamentos = apartamentoService.obtenerApartamentosPorCondominio(condominioId);
            return ResponseEntity.ok(apartamentos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()));
        }
    }

    // Obtener apartamento por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getApartamentoByUsuario(@PathVariable Integer usuarioId) {
        try {
            Apartamento apartamento = apartamentoService.obtenerApartamentoPorUsuario(usuarioId);
            if (apartamento == null) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Usuario no tiene apartamento asignado",
                        "apartamento", null));
            }
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "apartamento", apartamento));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()));
        }
    }

    // Crear apartamento
    @PostMapping
    public ResponseEntity<?> crearApartamento(@RequestBody Apartamento apartamento,
            @RequestParam Integer condominioId) {
        try {
            Apartamento nuevo = apartamentoService.crearApartamento(apartamento, condominioId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Apartamento creado exitosamente",
                    "apartamento", nuevo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()));
        }
    }

    // Actualizar apartamento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarApartamento(@PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {
        try {
            Apartamento actualizado = apartamentoService.actualizarApartamento(id, updates);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Apartamento actualizado exitosamente",
                    "apartamento", actualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()));
        }
    }

    // Eliminar apartamento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarApartamento(@PathVariable Integer id) {
        try {
            apartamentoService.eliminarApartamento(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Apartamento eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()));
        }
    }

    // Obtener estad??sticas del condominio
    @GetMapping("/estadisticas/{condominioId}")
    public ResponseEntity<?> getEstadisticas(@PathVariable Integer condominioId) {
        try {
            Map<String, Object> estadisticas = apartamentoService.obtenerEstadisticasCondominio(condominioId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "estadisticas", estadisticas));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()));
        }
    }
}