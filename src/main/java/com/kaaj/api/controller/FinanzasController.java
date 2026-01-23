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

    // ================ NUEVOS ENDPOINTS PARA CONTRALORÍA ================

    /**
     * Obtener meses disponibles con datos financieros
     */
    @GetMapping("/meses-disponibles")
    public ResponseEntity<?> getMesesDisponibles(
            @RequestParam(value = "condominioId", required = false) Long condominioId) {
        try {
            // Obtener todos los saldos (ingresos y egresos) para determinar meses con datos
            List<Saldo> todosSaldos = condominioId != null
                ? saldoRepository.findByUsuarioCondominioId(condominioId)
                : saldoRepository.findAll();

            // Extraer meses únicos con datos
            Set<YearMonth> mesesSet = todosSaldos.stream()
                .filter(saldo -> saldo.getFechaLimite() != null)
                .map(saldo -> YearMonth.from(saldo.getFechaLimite()))
                .collect(Collectors.toSet());

            // Convertir a lista ordenada
            List<Map<String, Object>> meses = mesesSet.stream()
                .sorted(Comparator.reverseOrder())
                .map(yearMonth -> {
                    Map<String, Object> mesData = new HashMap<>();
                    mesData.put("id", yearMonth.hashCode());
                    mesData.put("año", yearMonth.getYear());
                    mesData.put("mes", yearMonth.getMonthValue());
                    mesData.put("nombre", yearMonth.getMonth().getDisplayName(
                        java.time.format.TextStyle.FULL,
                        new Locale("es", "MX")
                    ));
                    mesData.put("value", String.format("%d-%02d",
                        yearMonth.getYear(),
                        yearMonth.getMonthValue()));

                    // Verificar si el mes está completo (tiene ingresos y egresos)
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

    /**
     * Obtener todos los ingresos (pagos recibidos)
     */
    @GetMapping("/ingresos")
    public ResponseEntity<?> getIngresos(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "mes", required = false) String mes, // Formato: YYYY-MM
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            LocalDate fechaInicio = null;
            LocalDate fechaFin = null;

            // Si se especifica mes, filtrar por ese mes
            if (mes != null) {
                String[] partes = mes.split("-");
                int year = Integer.parseInt(partes[0]);
                int month = Integer.parseInt(partes[1]);
                fechaInicio = LocalDate.of(year, month, 1);
                fechaFin = fechaInicio.withDayOfMonth(fechaInicio.lengthOfMonth());
            } else if (año != null) {
                // Si solo se especifica año, filtrar por ese año
                fechaInicio = LocalDate.of(año, 1, 1);
                fechaFin = LocalDate.of(año, 12, 31);
            }

            List<Object[]> resultados;
            if (condominioId != null) {
                if (fechaInicio != null && fechaFin != null) {
                    resultados = saldoRepository.findIngresosByCondominioAndFecha(
                        condominioId, fechaInicio, fechaFin);
                } else {
                    resultados = saldoRepository.findIngresosByCondominio(condominioId);
                }
            } else {
                if (fechaInicio != null && fechaFin != null) {
                    resultados = saldoRepository.findIngresosByFecha(fechaInicio, fechaFin);
                } else {
                    resultados = saldoRepository.findIngresosTotales();
                }
            }

            List<Map<String, Object>> ingresos = resultados.stream()
                .map(result -> {
                    Map<String, Object> ingreso = new HashMap<>();
                    ingreso.put("id", result[0]);
                    ingreso.put("concepto", result[1]);
                    ingreso.put("descripcion", result[2]);
                    ingreso.put("monto", result[3]);
                    ingreso.put("fecha", result[4]);

                    // Extraer mes y año de la fecha
                    if (result[4] != null) {
                        LocalDate fecha = (LocalDate) result[4];
                        ingreso.put("mes", fecha.getMonthValue());
                        ingreso.put("año", fecha.getYear());
                    }

                    ingreso.put("usuario", result[5]);
                    ingreso.put("comprobante", result[6]); // Asumiendo que hay un campo de comprobante
                    return ingreso;
                })
                .collect(Collectors.toList());

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

    /**
     * Obtener todos los egresos (gastos)
     */
    @GetMapping("/egresos")
    public ResponseEntity<?> getEgresos(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "mes", required = false) String mes,
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            LocalDate fechaInicio = null;
            LocalDate fechaFin = null;

            if (mes != null) {
                String[] partes = mes.split("-");
                int year = Integer.parseInt(partes[0]);
                int month = Integer.parseInt(partes[1]);
                fechaInicio = LocalDate.of(year, month, 1);
                fechaFin = fechaInicio.withDayOfMonth(fechaInicio.lengthOfMonth());
            } else if (año != null) {
                fechaInicio = LocalDate.of(año, 1, 1);
                fechaFin = LocalDate.of(año, 12, 31);
            }

            List<Object[]> resultados;
            if (condominioId != null) {
                if (fechaInicio != null && fechaFin != null) {
                    resultados = saldoRepository.findEgresosByCondominioAndFecha(
                        condominioId, fechaInicio, fechaFin);
                } else {
                    resultados = saldoRepository.findEgresosByCondominio(condominioId);
                }
            } else {
                if (fechaInicio != null && fechaFin != null) {
                    resultados = saldoRepository.findEgresosByFecha(fechaInicio, fechaFin);
                } else {
                    resultados = saldoRepository.findEgresosTotales();
                }
            }

            List<Map<String, Object>> egresos = resultados.stream()
                .map(result -> {
                    Map<String, Object> egreso = new HashMap<>();
                    egreso.put("id", result[0]);
                    egreso.put("concepto", result[1]);
                    egreso.put("descripcion", result[2]);
                    egreso.put("monto", result[3]);
                    egreso.put("fecha", result[4]);

                    if (result[4] != null) {
                        LocalDate fecha = (LocalDate) result[4];
                        egreso.put("mes", fecha.getMonthValue());
                        egreso.put("año", fecha.getYear());
                    }

                    egreso.put("proveedor", result[5]);
                    egreso.put("comprobante", result[6]);
                    return egreso;
                })
                .collect(Collectors.toList());

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

    /**
     * Obtener resumen mensual consolidado
     */
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

                if (totalIngresos == null) totalIngresos = BigDecimal.ZERO;
                if (totalEgresos == null) totalEgresos = BigDecimal.ZERO;

                // Contar transacciones
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
                    new Locale("es", "MX")
                ));

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

    /**
     * Obtener estadísticas por período específico (para gráficas)
     */
    @GetMapping("/estadisticas-periodo")
    public ResponseEntity<?> getEstadisticasPorPeriodoEspecifico(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(value = "agruparPor", defaultValue = "mes") String agruparPor) {
        try {
            List<Map<String, Object>> estadisticas = new ArrayList<>();

            if ("mes".equalsIgnoreCase(agruparPor)) {
                // Agrupar por mes
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

                    if (ingresos == null) ingresos = BigDecimal.ZERO;
                    if (egresos == null) egresos = BigDecimal.ZERO;

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
            } else if ("semana".equalsIgnoreCase(agruparPor)) {
                // Agrupar por semana
                // Implementación para agrupación por semana
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

    // ================ ENDPOINTS EXISTENTES (MANTENER) ================

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

            // Fecha límite (usamos fecha del gasto)
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