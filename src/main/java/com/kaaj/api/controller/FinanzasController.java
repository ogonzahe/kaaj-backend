package com.kaaj.api.controller;

import com.kaaj.api.dto.FinanzasDTO;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.repository.SaldoRepository;
import com.kaaj.api.repository.UsuarioRepository;
import com.kaaj.api.service.FinanzasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/finanzas")
public class FinanzasController {

    @Autowired
    private FinanzasService finanzasService;

    @Autowired
    private SaldoRepository saldoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ENDPOINTS PARA GASTOS (lo que necesita tu frontend)
    @GetMapping("/gastos")
    public ResponseEntity<?> getGastos() {
        try {
            List<Saldo> gastos = saldoRepository.findAll();

            List<Map<String, Object>> gastosResponse = gastos.stream()
                .map(gasto -> {
                    Map<String, Object> gastoMap = new HashMap<>();
                    gastoMap.put("id", gasto.getId());
                    gastoMap.put("concepto", gasto.getConcepto());
                    gastoMap.put("descripcion", gasto.getDescripcion());
                    gastoMap.put("monto", gasto.getMonto());
                    gastoMap.put("fecha", gasto.getFechaLimite());
                    gastoMap.put("condominio_id", gasto.getUsuario() != null && gasto.getUsuario().getCondominio() != null
                        ? gasto.getUsuario().getCondominio().getId() : null);
                    gastoMap.put("usuario_id", gasto.getUsuario() != null ? gasto.getUsuario().getId() : null);
                    return gastoMap;
                })
                .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", gastosResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener gastos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/gastos")
    public ResponseEntity<?> crearGasto(@RequestBody Map<String, Object> gastoData) {
        try {
            Saldo nuevoGasto = new Saldo();

            // Mapear datos del frontend
            nuevoGasto.setConcepto((String) gastoData.get("concepto"));
            nuevoGasto.setDescripcion((String) gastoData.get("descripcion"));

            // Convertir monto a BigDecimal
            Object montoObj = gastoData.get("monto");
            BigDecimal monto;
            if (montoObj instanceof Number) {
                monto = BigDecimal.valueOf(((Number) montoObj).doubleValue());
            } else if (montoObj instanceof String) {
                monto = new BigDecimal((String) montoObj);
            } else {
                monto = BigDecimal.ZERO;
            }
            nuevoGasto.setMonto(monto);

            // Fecha l??mite (usamos fecha del gasto)
            if (gastoData.get("fecha") != null) {
                nuevoGasto.setFechaLimite(LocalDate.parse((String) gastoData.get("fecha")));
            } else {
                nuevoGasto.setFechaLimite(LocalDate.now());
            }

            // Asociar usuario si existe
            if (gastoData.get("usuario_id") != null) {
                Long usuarioId = ((Number) gastoData.get("usuario_id")).longValue();
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(Math.toIntExact(usuarioId));
                usuarioOpt.ifPresent(nuevoGasto::setUsuario);
            }

            // Guardar el gasto
            Saldo gastoGuardado = saldoRepository.save(nuevoGasto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", gastoGuardado);
            response.put("message", "Gasto creado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al crear gasto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/gastos/{id}")
    public ResponseEntity<?> eliminarGasto(@PathVariable Long id) {
        try {
            if (!saldoRepository.existsById(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Gasto no encontrado");
                return ResponseEntity.status(404).body(error);
            }

            saldoRepository.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gasto eliminado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar gasto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ENDPOINTS ORIGINALES (mantenerlos)
    @GetMapping("/resumen")
    public ResponseEntity<?> getResumenFinanciero(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            Map<String, Object> resumen = finanzasService.getResumenFinanciero(condominioId, fechaInicio, fechaFin);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", resumen);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener resumen financiero: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/movimientos")
    public ResponseEntity<?> getMovimientosFinancieros(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Map<String, Object> movimientos = finanzasService.getMovimientosFinancieros(
                    condominioId, tipo, fechaInicio, fechaFin, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", movimientos);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener movimientos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> getEstadisticasFinancieras(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "periodo", defaultValue = "mes") String periodo,
            @RequestParam(value = "rango", defaultValue = "6") int rango) {

        try {
            List<FinanzasDTO> estadisticas = finanzasService.getEstadisticasPorPeriodo(condominioId, periodo, rango);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", estadisticas);
            response.put("periodo", periodo);
            response.put("rango", rango);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener estad??sticas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/detalle-ingresos")
    public ResponseEntity<?> getDetalleIngresos(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            List<Map<String, Object>> detalle = finanzasService.getDetalleIngresos(condominioId, fechaInicio, fechaFin);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", detalle);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener detalle de ingresos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/detalle-egresos")
    public ResponseEntity<?> getDetalleEgresos(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            List<Map<String, Object>> detalle = finanzasService.getDetalleEgresos(condominioId, fechaInicio, fechaFin);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", detalle);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener detalle de egresos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/saldos-pendientes")
    public ResponseEntity<?> getSaldosPendientes(
            @RequestParam(value = "condominioId", required = false) Long condominioId) {

        try {
            List<Map<String, Object>> saldos = finanzasService.getSaldosPendientes(condominioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", saldos);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener saldos pendientes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}