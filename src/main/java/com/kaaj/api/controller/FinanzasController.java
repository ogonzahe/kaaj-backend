package com.kaaj.api.controller;

import com.kaaj.api.dto.FinanzasDTO;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.model.Egreso;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.repository.CondominioRepository;
import com.kaaj.api.repository.EgresoRepository;
import com.kaaj.api.repository.SaldoRepository;
import com.kaaj.api.repository.UsuarioRepository;
import com.kaaj.api.service.FinanzasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/finanzas")
public class FinanzasController {

    private final FinanzasService finanzasService;
    private final SaldoRepository saldoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EgresoRepository egresoRepository;
    private final CondominioRepository condominioRepository;

    @GetMapping("/ingresos")
    public ResponseEntity<?> getAllIngresos(
            @RequestParam(value = "condominioIds", required = false) String condominioIdsRaw,
            @RequestParam(value = "condominioId", required = false) Long condominioIdSingle,
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            List<Long> condominioIds = parseCondominioIds(condominioIdsRaw);
            if (condominioIds == null && condominioIdSingle != null) {
                condominioIds = List.of(condominioIdSingle);
            }
            log.info("GET /api/finanzas/ingresos llamado (condominioIds={}, año={})", condominioIds, año);

            List<Saldo> ingresosPagados;
            if (condominioIds != null && año != null) {
                ingresosPagados = saldoRepository.findIngresosPagadosByCondominioIdsAndAnio(condominioIds, año);
            } else if (condominioIds != null) {
                ingresosPagados = saldoRepository.findIngresosPagadosByCondominioIds(condominioIds);
            } else if (año != null) {
                ingresosPagados = saldoRepository.findIngresosPagadosByAnio(año);
            } else {
                ingresosPagados = saldoRepository.findIngresosPagados();
            }

            List<Map<String, Object>> ingresos = ingresosPagados.stream()
                    .map(this::mapSaldoToFinanzaRow)
                    .toList();

            log.info("Ingresos (saldos pagados) encontrados: {}", ingresos.size());

            BigDecimal totalMonto = ingresos.stream()
                    .map(i -> (BigDecimal) i.get("monto"))
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ingresos);
            response.put("total", ingresos.size());
            response.put("totalMonto", totalMonto);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en GET /api/finanzas/ingresos", e);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener ingresos");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/por-cobrar")
    public ResponseEntity<?> getPorCobrar(
            @RequestParam(value = "condominioIds", required = false) String condominioIdsRaw,
            @RequestParam(value = "condominioId", required = false) Long condominioIdSingle,
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            List<Long> condominioIds = parseCondominioIds(condominioIdsRaw);
            if (condominioIds == null && condominioIdSingle != null) {
                condominioIds = List.of(condominioIdSingle);
            }
            log.info("GET /api/finanzas/por-cobrar (condominioIds={}, año={})", condominioIds, año);

            List<Saldo> pendientes;
            if (condominioIds != null && año != null) {
                pendientes = saldoRepository.findPorCobrarByCondominioIdsAndAnio(condominioIds, año);
            } else if (condominioIds != null) {
                pendientes = saldoRepository.findPorCobrarByCondominioIds(condominioIds);
            } else if (año != null) {
                pendientes = saldoRepository.findPorCobrarByAnio(año);
            } else {
                pendientes = saldoRepository.findPorCobrar();
            }

            List<Map<String, Object>> data = pendientes.stream()
                    .map(this::mapSaldoToFinanzaRow)
                    .toList();

            BigDecimal totalMonto = data.stream()
                    .map(i -> (BigDecimal) i.get("monto"))
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("total", data.size());
            response.put("totalMonto", totalMonto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en GET /api/finanzas/por-cobrar", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener saldos por cobrar");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    private Map<String, Object> mapSaldoToFinanzaRow(Saldo saldo) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", saldo.getId());
        map.put("concepto", saldo.getConcepto());
        map.put("descripcion", saldo.getDescripcion());
        map.put("monto", saldo.getMonto());
        map.put("fecha", saldo.getFechaPago() != null ? saldo.getFechaPago() : saldo.getFechaLimite());
        map.put("fechaLimite", saldo.getFechaLimite());
        map.put("pagado", saldo.getPagado());
        Long condominioId = null;
        if (saldo.getUsuario() != null && saldo.getUsuario().getCondominio() != null
                && saldo.getUsuario().getCondominio().getId() != null) {
            condominioId = saldo.getUsuario().getCondominio().getId().longValue();
        } else if (saldo.getCondominio() != null && saldo.getCondominio().getId() != null) {
            condominioId = saldo.getCondominio().getId().longValue();
        }
        map.put("condominio_id", condominioId);
        map.put("usuario_id", saldo.getUsuario() != null ? saldo.getUsuario().getId() : null);
        return map;
    }

    @GetMapping("/egresos")
    public ResponseEntity<?> getAllEgresos(
            @RequestParam(value = "condominioIds", required = false) String condominioIdsRaw,
            @RequestParam(value = "condominioId", required = false) Long condominioIdSingle,
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            List<Long> condominioIds = parseCondominioIds(condominioIdsRaw);
            if (condominioIds == null && condominioIdSingle != null) {
                condominioIds = List.of(condominioIdSingle);
            }
            log.info("GET /api/finanzas/egresos llamado (condominioIds={}, año={})", condominioIds, año);

            List<Egreso> egresosEntities;
            if (condominioIds != null && año != null) {
                egresosEntities = egresoRepository.findByCondominioIdInAndAnio(condominioIds, año);
            } else if (condominioIds != null) {
                egresosEntities = egresoRepository.findByCondominioIdIn(condominioIds);
            } else if (año != null) {
                egresosEntities = egresoRepository.findByAnio(año);
            } else {
                egresosEntities = egresoRepository.findAll();
            }

            List<Map<String, Object>> egresos = egresosEntities.stream()
                    .map(this::mapEgresoToRow)
                    .toList();

            log.info("Egresos encontrados: {}", egresos.size());

            BigDecimal totalMonto = egresos.stream()
                    .map(e -> (BigDecimal) e.get("monto"))
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", egresos);
            response.put("total", egresos.size());
            response.put("totalMonto", totalMonto);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en GET /api/finanzas/egresos", e);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener egresos");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    private Map<String, Object> mapEgresoToRow(Egreso egreso) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", egreso.getId());
        map.put("concepto", egreso.getConcepto());
        map.put("descripcion", egreso.getDescripcion());
        map.put("monto", egreso.getMonto() != null ? egreso.getMonto() : BigDecimal.ZERO);
        map.put("fecha", egreso.getFecha() != null ? egreso.getFecha().toString() : null);
        map.put("mes", egreso.getMes());
        map.put("año", egreso.getAño());
        map.put("estatus", egreso.getEstatus());
        map.put("condominio_id",
                egreso.getCondominio() != null ? egreso.getCondominio().getId() : null);
        map.put("usuario_id",
                egreso.getUsuario() != null ? egreso.getUsuario().getId() : null);
        return map;
    }

    @GetMapping("/gastos")
    public ResponseEntity<?> getGastos(
            @RequestParam(value = "condominioIds", required = false) String condominioIdsRaw,
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            List<Long> condominioIds = parseCondominioIds(condominioIdsRaw);
            log.info("GET /api/finanzas/gastos llamado (condominioIds={}, año={})", condominioIds, año);

            List<Saldo> gastos;
            if (condominioIds != null && año != null) {
                gastos = saldoRepository.findByUsuarioCondominioIdInAndAnio(condominioIds, año);
            } else if (condominioIds != null) {
                gastos = saldoRepository.findByUsuarioCondominioIdIn(condominioIds);
            } else if (año != null) {
                gastos = saldoRepository.findByAnio(año);
            } else {
                gastos = saldoRepository.findAll();
            }

            log.info("Gastos encontrados: {}", gastos.size());

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
                                        : gasto.getCondominio() != null ? gasto.getCondominio().getId() : null);
                        gastoMap.put("usuario_id", gasto.getUsuario() != null ? gasto.getUsuario().getId() : null);
                        return gastoMap;
                    })
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", gastosResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en GET /api/finanzas/gastos", e);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener gastos");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/ingresos-filtrados")
    public ResponseEntity<?> getIngresosFiltrados(
            @RequestParam("condominioId") Long condominioId,
            @RequestParam(value = "año", required = false) Integer año,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "periodo", required = false) String periodo) {
        try {
            log.info("GET /api/finanzas/ingresos-filtrados llamado: condominioId={}", condominioId);

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
            log.error("Error en GET /api/finanzas/ingresos-filtrados", e);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener ingresos filtrados");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/egresos-filtrados")
    public ResponseEntity<?> getEgresosFiltrados(
            @RequestParam("condominioId") Long condominioId,
            @RequestParam(value = "año", required = false) Integer año,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "periodo", required = false) String periodo) {
        try {
            log.info("GET /api/finanzas/egresos-filtrados llamado: condominioId={}", condominioId);

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
            log.error("Error en GET /api/finanzas/egresos-filtrados", e);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener egresos filtrados");
            return ResponseEntity.internalServerError().body(error);
        }
    }

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
            log.error("Error al obtener meses disponibles", e);
            error.put("message", "Error al obtener meses disponibles");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/resumen-mensual")
    public ResponseEntity<?> getResumenMensual(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominioIds", required = false) String condominioIdsRaw,
            @RequestParam(value = "año", required = false) Integer año) {
        try {
            int targetYear = año != null ? año : LocalDate.now().getYear();
            List<Long> condominioIds = parseCondominioIds(condominioIdsRaw);
            boolean usarMulti = condominioIds != null;

            List<Map<String, Object>> resumenMensual = new ArrayList<>();

            for (int month = 1; month <= 12; month++) {
                LocalDate inicioMes = LocalDate.of(targetYear, month, 1);
                LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

                BigDecimal totalIngresos;
                BigDecimal totalPorCobrar;
                BigDecimal totalEgresos;
                long countIngresos;
                long countPorCobrar;
                long countEgresos;

                if (usarMulti) {
                    totalIngresos = saldoRepository.calcularIngresosCondominios(condominioIds, inicioMes, finMes);
                    totalPorCobrar = saldoRepository.calcularEgresosCondominios(condominioIds, inicioMes, finMes);
                    totalEgresos = egresoRepository.calcularEgresosCondominiosPorFecha(condominioIds, inicioMes, finMes);
                    countIngresos = saldoRepository.countIngresosCondominios(condominioIds, inicioMes, finMes);
                    countPorCobrar = saldoRepository.countEgresosCondominios(condominioIds, inicioMes, finMes);
                    countEgresos = egresoRepository.countEgresosCondominiosPorFecha(condominioIds, inicioMes, finMes);
                } else if (condominioId != null) {
                    totalIngresos = saldoRepository.calcularIngresosCondominio(condominioId, inicioMes, finMes);
                    totalPorCobrar = saldoRepository.calcularEgresosCondominio(condominioId, inicioMes, finMes);
                    totalEgresos = egresoRepository.calcularEgresosCondominioPorFecha(condominioId, inicioMes, finMes);
                    countIngresos = saldoRepository.countIngresosCondominio(condominioId, inicioMes, finMes);
                    countPorCobrar = saldoRepository.countEgresosCondominio(condominioId, inicioMes, finMes);
                    countEgresos = egresoRepository.countEgresosCondominioPorFecha(condominioId, inicioMes, finMes);
                } else {
                    totalIngresos = saldoRepository.calcularIngresosTotales(inicioMes, finMes);
                    totalPorCobrar = saldoRepository.calcularEgresosTotales(inicioMes, finMes);
                    totalEgresos = egresoRepository.calcularEgresosTotalesPorFecha(inicioMes, finMes);
                    countIngresos = saldoRepository.countIngresosTotales(inicioMes, finMes);
                    countPorCobrar = saldoRepository.countEgresosTotales(inicioMes, finMes);
                    countEgresos = egresoRepository.countEgresosTotalesPorFecha(inicioMes, finMes);
                }

                if (totalIngresos == null) totalIngresos = BigDecimal.ZERO;
                if (totalPorCobrar == null) totalPorCobrar = BigDecimal.ZERO;
                if (totalEgresos == null) totalEgresos = BigDecimal.ZERO;

                Map<String, Object> mesData = new HashMap<>();
                mesData.put("mes", month);
                mesData.put("año", targetYear);
                mesData.put("totalIngresos", totalIngresos);
                mesData.put("totalEgresos", totalEgresos);
                mesData.put("totalPorCobrar", totalPorCobrar);
                mesData.put("balance", totalIngresos.subtract(totalEgresos));
                mesData.put("cantidadTransacciones", countIngresos + countEgresos + countPorCobrar);
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
            log.error("Error al obtener resumen mensual", e);
            error.put("message", "Error al obtener resumen mensual");
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
            log.error("Error al obtener estadisticas por periodo", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener estadísticas");
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
            log.error("Error al crear gasto", e);
            error.put("message", "Error al crear gasto");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/egresos")
    public ResponseEntity<?> crearEgreso(@RequestBody Map<String, Object> egresoData) {
        try {
            Egreso nuevoEgreso = new Egreso();
            nuevoEgreso.setConcepto((String) egresoData.get("concepto"));
            nuevoEgreso.setDescripcion((String) egresoData.get("descripcion"));

            Object montoObj = egresoData.get("monto");
            BigDecimal monto;
            if (montoObj instanceof Number) {
                monto = BigDecimal.valueOf(((Number) montoObj).doubleValue());
            } else if (montoObj instanceof String) {
                monto = new BigDecimal((String) montoObj);
            } else {
                monto = BigDecimal.ZERO;
            }
            nuevoEgreso.setMonto(monto);

            LocalDate fecha;
            if (egresoData.get("fecha") != null) {
                fecha = LocalDate.parse((String) egresoData.get("fecha"));
            } else {
                fecha = LocalDate.now();
            }
            nuevoEgreso.setFecha(fecha);
            nuevoEgreso.setMes(fecha.getMonthValue());
            nuevoEgreso.setAño(fecha.getYear());

            if (egresoData.get("estatus") != null) {
                nuevoEgreso.setEstatus((String) egresoData.get("estatus"));
            } else {
                nuevoEgreso.setEstatus("pagado");
            }

            Object condominioIdObj = egresoData.get("condominio_id");
            if (condominioIdObj == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "condominio_id es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }
            int condominioIdInt = ((Number) condominioIdObj).intValue();
            Optional<Condominio> condominioOpt = condominioRepository.findById(condominioIdInt);
            if (condominioOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Condominio no encontrado");
                return ResponseEntity.status(404).body(error);
            }
            nuevoEgreso.setCondominio(condominioOpt.get());

            if (egresoData.get("usuario_id") != null) {
                int usuarioId = ((Number) egresoData.get("usuario_id")).intValue();
                usuarioRepository.findById(usuarioId).ifPresent(nuevoEgreso::setUsuario);
            }

            Egreso guardado = egresoRepository.save(nuevoEgreso);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mapEgresoToRow(guardado));
            response.put("message", "Egreso creado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al crear egreso", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al crear egreso");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/egresos/{id}")
    public ResponseEntity<?> eliminarEgreso(@PathVariable Long id) {
        try {
            if (!egresoRepository.existsById(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Egreso no encontrado");
                return ResponseEntity.status(404).body(error);
            }

            egresoRepository.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Egreso eliminado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al eliminar egreso", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar egreso");
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
            log.error("Error al eliminar gasto", e);
            error.put("message", "Error al eliminar gasto");
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
            log.error("Error al obtener resumen financiero", e);
            error.put("message", "Error al obtener resumen financiero");
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
            log.error("Error al obtener movimientos", e);
            error.put("message", "Error al obtener movimientos");
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
            log.error("Error al obtener estadisticas financieras", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener estadísticas");
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
            log.error("Error al obtener detalle de ingresos", e);
            error.put("message", "Error al obtener detalle de ingresos");
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
            log.error("Error al obtener detalle de egresos", e);
            error.put("message", "Error al obtener detalle de egresos");
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
            log.error("Error al obtener saldos pendientes", e);
            error.put("message", "Error al obtener saldos pendientes");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    private List<Long> parseCondominioIds(String raw) {
        if (raw == null || raw.isBlank()) return null;
        List<Long> ids = new ArrayList<>();
        for (String token : raw.split(",")) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) continue;
            try {
                ids.add(Long.parseLong(trimmed));
            } catch (NumberFormatException ignored) {
                log.warn("condominioIds: token no numérico ignorado: '{}'", trimmed);
            }
        }
        return ids.isEmpty() ? null : ids;
    }
}