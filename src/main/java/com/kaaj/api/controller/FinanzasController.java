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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/meses-disponibles")
    public ResponseEntity<?> getMesesDisponibles(
            @RequestParam(value = "condominioId", required = false) Long condominioId) {
        try {
            List<Saldo> todosSaldos = condominioId != null
                    ? saldoRepository.findByUsuarioCondominioId(condominioId)
                    : saldoRepository.findAll();

            Set<YearMonth> mesesSet = todosSaldos.stream()
                    .filter(saldo -> saldo.getFechaLimite() != null)
                    .map(saldo -> YearMonth.from(saldo.getFechaLimite()))
                    .collect(Collectors.toSet());

            List<Map<String, Object>> meses = mesesSet.stream()
                    .sorted(Comparator.reverseOrder())
                    .map(yearMonth -> {
                        Map<String, Object> mesData = new HashMap<>();
                        mesData.put("id", yearMonth.hashCode());
                        mesData.put("año", yearMonth.getYear());
                        mesData.put("mes", yearMonth.getMonthValue());
                        mesData.put("nombre", yearMonth.getMonth().getDisplayName(
                                java.time.format.TextStyle.FULL,
                                new Locale("es", "MX")));
                        mesData.put("value", String.format("%d-%02d",
                                yearMonth.getYear(),
                                yearMonth.getMonthValue()));

                        long ingresosCount = todosSaldos.stream()
                                .filter(s -> YearMonth.from(s.getFechaLimite()).equals(yearMonth)
                                        && s.getMonto().compareTo(BigDecimal.ZERO) > 0)
                                .count();
                        long egresosCount = todosSaldos.stream()
                                .filter(s -> YearMonth.from(s.getFechaLimite()).equals(yearMonth)
                                        && s.getMonto().compareTo(BigDecimal.ZERO) < 0)
                                .count();

                        mesData.put("completo", ingresosCount > 0 && egresosCount > 0);
                        return mesData;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", meses);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener meses disponibles: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/ingresos")
    public ResponseEntity<?> getIngresos(
            @RequestParam("condominioId") Long condominioId,
            @RequestParam(value = "año", required = false) Integer año,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "periodo", required = false) String periodo) {
        try {
            List<Map<String, Object>> ingresos = finanzasService.getIngresosDetalladosNuevo(condominioId, año, mes,
                    periodo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ingresos);
            response.put("total", ingresos.size());
            response.put("totalMonto", ingresos.stream()
                    .map(i -> (BigDecimal) i.get("monto"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener ingresos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/egresos")
    public ResponseEntity<?> getEgresos(
            @RequestParam("condominioId") Long condominioId,
            @RequestParam(value = "año", required = false) Integer año,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "periodo", required = false) String periodo) {
        try {
            List<Map<String, Object>> egresos = finanzasService.getEgresosDetalladosNuevo(condominioId, año, mes,
                    periodo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", egresos);
            response.put("total", egresos.size());
            response.put("totalMonto", egresos.stream()
                    .map(e -> (BigDecimal) e.get("monto"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener egresos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/resumen-mensual")
    public ResponseEntity<?> getResumenMensual(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            int targetYear = año != null ? año : LocalDate.now().getYear();

            List<Map<String, Object>> resumenMensual = new ArrayList<>();

            for (int month = 1; month <= 12; month++) {
                LocalDate inicioMes = LocalDate.of(targetYear, month, 1);
                LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

                BigDecimal totalIngresos = condominioId != null
                        ? saldoRepository.calcularIngresosCondominio(condominioId, inicioMes, finMes)
                        : saldoRepository.calcularIngresosTotales(inicioMes, finMes);

                BigDecimal totalEgresos = condominioId != null
                        ? saldoRepository.calcularEgresosCondominio(condominioId, inicioMes, finMes)
                        : saldoRepository.calcularEgresosTotales(inicioMes, finMes);

                if (totalIngresos == null)
                    totalIngresos = BigDecimal.ZERO;
                if (totalEgresos == null)
                    totalEgresos = BigDecimal.ZERO;

                long countIngresos = condominioId != null
                        ? saldoRepository.countIngresosCondominio(condominioId, inicioMes, finMes)
                        : saldoRepository.countIngresosTotales(inicioMes, finMes);

                long countEgresos = condominioId != null
                        ? saldoRepository.countEgresosCondominio(condominioId, inicioMes, finMes)
                        : saldoRepository.countEgresosTotales(inicioMes, finMes);

                Map<String, Object> mesData = new HashMap<>();
                mesData.put("mes", month);
                mesData.put("año", targetYear);
                mesData.put("totalIngresos", totalIngresos);
                mesData.put("totalEgresos", totalEgresos);
                mesData.put("balance", totalIngresos.subtract(totalEgresos));
                mesData.put("cantidadTransacciones", countIngresos + countEgresos);
                mesData.put("nombreMes", inicioMes.getMonth().getDisplayName(
                        java.time.format.TextStyle.FULL,
                        new Locale("es", "MX")));

                resumenMensual.add(mesData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", resumenMensual);
            response.put("año", targetYear);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener resumen mensual: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/estadisticas-periodo")
    public ResponseEntity<?> getEstadisticasPorPeriodoEspecifico(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(value = "agruparPor", defaultValue = "mes") String agruparPor) {
        try {
            List<Map<String, Object>> estadisticas = new ArrayList<>();

            if ("mes".equalsIgnoreCase(agruparPor)) {
                YearMonth inicio = YearMonth.from(fechaInicio);
                YearMonth fin = YearMonth.from(fechaFin);

                for (YearMonth current = inicio; !current.isAfter(fin); current = current.plusMonths(1)) {
                    LocalDate inicioMes = current.atDay(1);
                    LocalDate finMes = current.atEndOfMonth();

                    BigDecimal ingresos = condominioId != null
                            ? saldoRepository.calcularIngresosCondominio(condominioId, inicioMes, finMes)
                            : saldoRepository.calcularIngresosTotales(inicioMes, finMes);

                    BigDecimal egresos = condominioId != null
                            ? saldoRepository.calcularEgresosCondominio(condominioId, inicioMes, finMes)
                            : saldoRepository.calcularEgresosTotales(inicioMes, finMes);

                    if (ingresos == null)
                        ingresos = BigDecimal.ZERO;
                    if (egresos == null)
                        egresos = BigDecimal.ZERO;

                    Map<String, Object> periodoData = new HashMap<>();
                    periodoData.put("periodo", current.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                    periodoData.put("label", current.getMonth().getDisplayName(
                            java.time.format.TextStyle.FULL,
                            new Locale("es", "MX")) + " " + current.getYear());
                    periodoData.put("ingresos", ingresos);
                    periodoData.put("egresos", egresos);
                    periodoData.put("balance", ingresos.subtract(egresos));

                    estadisticas.add(periodoData);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", estadisticas);
            response.put("agruparPor", agruparPor);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

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
                        gastoMap.put("condominio_id",
                                gasto.getUsuario() != null && gasto.getUsuario().getCondominio() != null
                                        ? gasto.getUsuario().getCondominio().getId()
                                        : null);
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

            nuevoGasto.setConcepto((String) gastoData.get("concepto"));
            nuevoGasto.setDescripcion((String) gastoData.get("descripcion"));

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

            if (gastoData.get("fecha") != null) {
                nuevoGasto.setFechaLimite(LocalDate.parse((String) gastoData.get("fecha")));
            } else {
                nuevoGasto.setFechaLimite(LocalDate.now());
            }

            if (gastoData.get("usuario_id") != null) {
                Long usuarioId = ((Number) gastoData.get("usuario_id")).longValue();
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(Math.toIntExact(usuarioId));
                usuarioOpt.ifPresent(nuevoGasto::setUsuario);
            }

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
            @RequestParam("condominioId") Long condominioId,
            @RequestParam(value = "periodo", defaultValue = "mes") String periodo,
            @RequestParam(value = "rango", defaultValue = "3") int rango,
            @RequestParam(value = "año", required = false) Integer año,
            @RequestParam(value = "mes", required = false) Integer mes) {

        try {
            List<FinanzasDTO> estadisticas = finanzasService.getEstadisticasPorPeriodo(condominioId, periodo, rango,
                    año, mes);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", estadisticas);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener estadísticas: " + e.getMessage());
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
            @RequestParam("condominioId") Long condominioId) {

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