package com.kaaj.api.service;

import com.kaaj.api.dto.FinanzasDTO;
import com.kaaj.api.model.Ingreso;
import com.kaaj.api.model.Egreso;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.repository.IngresoRepository;
import com.kaaj.api.repository.EgresoRepository;
import com.kaaj.api.repository.SaldoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanzasService {

    @Autowired
    private SaldoRepository saldoRepository;

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private EgresoRepository egresoRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getResumenFinanciero(Long condominioId, LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;

        if (condominioId != null) {
            totalIngresos = saldoRepository.calcularIngresosCondominio(condominioId, fechaInicio, fechaFin);
            if (totalIngresos == null)
                totalIngresos = BigDecimal.ZERO;

            totalEgresos = saldoRepository.calcularEgresosCondominio(condominioId, fechaInicio, fechaFin);
            if (totalEgresos == null)
                totalEgresos = BigDecimal.ZERO;
        } else {
            totalIngresos = saldoRepository.calcularIngresosTotales(fechaInicio, fechaFin);
            if (totalIngresos == null)
                totalIngresos = BigDecimal.ZERO;

            totalEgresos = saldoRepository.calcularEgresosTotales(fechaInicio, fechaFin);
            if (totalEgresos == null)
                totalEgresos = BigDecimal.ZERO;
        }

        BigDecimal balance = totalIngresos.subtract(totalEgresos);

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalIngresos", totalIngresos);
        resumen.put("totalEgresos", totalEgresos);
        resumen.put("balance", balance);
        resumen.put("fechaInicio", fechaInicio);
        resumen.put("fechaFin", fechaFin);
        resumen.put("condominioId", condominioId);

        return resumen;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMovimientosFinancieros(Long condominioId, String tipo,
            LocalDate fechaInicio, LocalDate fechaFin, int page, int size) {

        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> resultadosPage;

        if (condominioId != null) {
            if ("ingreso".equalsIgnoreCase(tipo)) {
                resultadosPage = saldoRepository.findIngresosByCondominio(condominioId, fechaInicio, fechaFin,
                        pageable);
            } else if ("egreso".equalsIgnoreCase(tipo)) {
                resultadosPage = saldoRepository.findEgresosByCondominio(condominioId, fechaInicio, fechaFin, pageable);
            } else {
                resultadosPage = saldoRepository.findAllMovimientosByCondominio(condominioId, fechaInicio, fechaFin,
                        pageable);
            }
        } else {
            if ("ingreso".equalsIgnoreCase(tipo)) {
                resultadosPage = saldoRepository.findIngresosTotales(fechaInicio, fechaFin, pageable);
            } else if ("egreso".equalsIgnoreCase(tipo)) {
                resultadosPage = saldoRepository.findEgresosTotales(fechaInicio, fechaFin, pageable);
            } else {
                resultadosPage = saldoRepository.findAllMovimientos(fechaInicio, fechaFin, pageable);
            }
        }

        List<Map<String, Object>> movimientos = resultadosPage.getContent().stream()
                .map(result -> {
                    Map<String, Object> movimiento = new HashMap<>();
                    movimiento.put("id", result[0]);
                    movimiento.put("tipo", result[1]);
                    movimiento.put("concepto", result[2]);
                    movimiento.put("monto", result[3]);
                    movimiento.put("fecha", result[4]);
                    movimiento.put("usuario", result[5]);
                    movimiento.put("condominio", result[6]);
                    return movimiento;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("movimientos", movimientos);
        response.put("currentPage", resultadosPage.getNumber());
        response.put("totalItems", resultadosPage.getTotalElements());
        response.put("totalPages", resultadosPage.getTotalPages());
        response.put("pageSize", size);

        return response;
    }

    @Transactional(readOnly = true)
    public List<FinanzasDTO> getEstadisticasPorPeriodo(Long condominioId, String periodo, int rango) {
        return getEstadisticasPorPeriodo(condominioId, periodo, rango, null, null);
    }

    @Transactional(readOnly = true)
    public List<FinanzasDTO> getEstadisticasPorPeriodo(Long condominioId, String periodo, int rango, Integer año,
            Integer mes) {
        List<FinanzasDTO> estadisticas = new ArrayList<>();

        if ("mes".equals(periodo) && año != null && mes != null) {
            return getEstadisticasMensuales(condominioId, año, mes, rango);
        } else if ("anual".equals(periodo) && año != null) {
            return getEstadisticasAnuales(condominioId, año);
        } else if ("3meses".equals(periodo) || "6meses".equals(periodo) || "12meses".equals(periodo)) {
            return getEstadisticasUltimosMeses(condominioId, rango);
        }

        return getEstadisticasDefault(condominioId, periodo, rango);
    }

    private List<FinanzasDTO> getEstadisticasMensuales(Long condominioId, Integer año, Integer mes, int rango) {
        List<FinanzasDTO> estadisticas = new ArrayList<>();
        int mesInicio = Math.max(1, mes - rango + 1);

        for (int m = mesInicio; m <= mes; m++) {
            FinanzasDTO dto = new FinanzasDTO();
            dto.setPeriodo(getNombreMes(m));

            BigDecimal ingresos = ingresoRepository.calcularTotalPorMes(condominioId, año, m);
            BigDecimal egresos = egresoRepository.calcularTotalPorMes(condominioId, año, m);

            dto.setIngresos(ingresos != null ? ingresos : BigDecimal.ZERO);
            dto.setEgresos(egresos != null ? egresos : BigDecimal.ZERO);

            estadisticas.add(dto);
        }

        return estadisticas;
    }

    private List<FinanzasDTO> getEstadisticasAnuales(Long condominioId, Integer año) {
        List<FinanzasDTO> estadisticas = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            FinanzasDTO dto = new FinanzasDTO();
            dto.setPeriodo(getNombreMes(m));

            BigDecimal ingresos = ingresoRepository.calcularTotalPorMes(condominioId, año, m);
            BigDecimal egresos = egresoRepository.calcularTotalPorMes(condominioId, año, m);

            dto.setIngresos(ingresos != null ? ingresos : BigDecimal.ZERO);
            dto.setEgresos(egresos != null ? egresos : BigDecimal.ZERO);

            estadisticas.add(dto);
        }

        return estadisticas;
    }

    private List<FinanzasDTO> getEstadisticasUltimosMeses(Long condominioId, int rango) {
        List<FinanzasDTO> estadisticas = new ArrayList<>();
        LocalDate ahora = LocalDate.now();
        int añoActual = ahora.getYear();
        int mesActual = ahora.getMonthValue();

        for (int i = rango - 1; i >= 0; i--) {
            YearMonth ym = YearMonth.of(añoActual, mesActual).minusMonths(i);
            int año = ym.getYear();
            int mes = ym.getMonthValue();

            FinanzasDTO dto = new FinanzasDTO();
            dto.setPeriodo(getNombreMes(mes));

            BigDecimal ingresos = ingresoRepository.calcularTotalPorMes(condominioId, año, mes);
            BigDecimal egresos = egresoRepository.calcularTotalPorMes(condominioId, año, mes);

            dto.setIngresos(ingresos != null ? ingresos : BigDecimal.ZERO);
            dto.setEgresos(egresos != null ? egresos : BigDecimal.ZERO);

            estadisticas.add(dto);
        }

        return estadisticas;
    }

    private List<FinanzasDTO> getEstadisticasDefault(Long condominioId, String periodo, int rango) {
        List<FinanzasDTO> estadisticas = new ArrayList<>();
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter;

        for (int i = rango - 1; i >= 0; i--) {
            LocalDate fechaPeriodo;
            String label;

            if ("mes".equalsIgnoreCase(periodo)) {
                fechaPeriodo = fechaActual.minusMonths(i);
                YearMonth yearMonth = YearMonth.from(fechaPeriodo);
                label = fechaPeriodo.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                fechaPeriodo = yearMonth.atDay(1);
            } else if ("semana".equalsIgnoreCase(periodo)) {
                fechaPeriodo = fechaActual.minusWeeks(i);
                label = "Semana " + fechaPeriodo.format(DateTimeFormatter.ofPattern("ww/yyyy"));
                fechaPeriodo = fechaPeriodo.with(java.time.DayOfWeek.MONDAY);
            } else {
                fechaPeriodo = fechaActual.minusDays(i);
                label = fechaPeriodo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            FinanzasDTO dto = new FinanzasDTO();
            dto.setPeriodo(label);
            dto.setFecha(fechaPeriodo);

            BigDecimal ingresos = condominioId != null
                    ? saldoRepository.calcularIngresosCondominio(condominioId, fechaPeriodo, fechaPeriodo.plusMonths(1))
                    : saldoRepository.calcularIngresosTotales(fechaPeriodo, fechaPeriodo.plusMonths(1));
            dto.setIngresos(ingresos != null ? ingresos : BigDecimal.ZERO);

            BigDecimal egresos = condominioId != null
                    ? saldoRepository.calcularEgresosCondominio(condominioId, fechaPeriodo, fechaPeriodo.plusMonths(1))
                    : saldoRepository.calcularEgresosTotales(fechaPeriodo, fechaPeriodo.plusMonths(1));
            dto.setEgresos(egresos != null ? egresos : BigDecimal.ZERO);

            estadisticas.add(dto);
        }

        return estadisticas;
    }

    private String getNombreMes(int mes) {
        String[] meses = { "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
        return meses[mes - 1];
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDetalleIngresos(Long condominioId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Object[]> resultados = condominioId != null
                ? saldoRepository.findDetalleIngresosCondominio(condominioId, fechaInicio, fechaFin)
                : saldoRepository.findDetalleIngresosTotales(fechaInicio, fechaFin);

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("concepto", result[0]);
                    detalle.put("monto", result[1]);
                    detalle.put("porcentaje", result[2]);
                    detalle.put("cantidad", result[3]);
                    return detalle;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDetalleEgresos(Long condominioId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Object[]> resultados = condominioId != null
                ? saldoRepository.findDetalleEgresosCondominio(condominioId, fechaInicio, fechaFin)
                : saldoRepository.findDetalleEgresosTotales(fechaInicio, fechaFin);

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("concepto", result[0]);
                    detalle.put("monto", result[1]);
                    detalle.put("porcentaje", result[2]);
                    detalle.put("cantidad", result[3]);
                    return detalle;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSaldosPendientes(Long condominioId) {
        List<Saldo> saldos = condominioId != null ? saldoRepository.findSaldosPendientesCondominio(condominioId)
                : saldoRepository.findSaldosPendientes();

        return saldos.stream()
                .map(saldo -> {
                    Map<String, Object> saldoMap = new HashMap<>();
                    saldoMap.put("id", saldo.getId());
                    saldoMap.put("concepto", saldo.getConcepto());
                    saldoMap.put("descripcion", saldo.getDescripcion());
                    saldoMap.put("monto", saldo.getMonto());
                    saldoMap.put("saldoActual", saldo.getSaldoActual());
                    saldoMap.put("fechaLimite", saldo.getFechaLimite());
                    saldoMap.put("usuario", saldo.getUsuario().getNombre());
                    saldoMap.put("condominio",
                            saldo.getUsuario().getCondominio() != null ? saldo.getUsuario().getCondominio().getNombre()
                                    : "N/A");
                    return saldoMap;
                })
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS NUEVOS PARA LAS NUEVAS TABLAS ==========

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getIngresosDetalladosNuevo(Long condominioId, Integer año, Integer mes,
            String periodo) {
        List<Ingreso> ingresos;

        if (año != null && mes != null) {
            ingresos = ingresoRepository.findByCondominioIdAndAñoAndMes(condominioId, año, mes);
        } else if (año != null) {
            ingresos = ingresoRepository.findByCondominioIdAndAño(condominioId, año);
        } else {
            ingresos = ingresoRepository.findByCondominioId(condominioId);
        }

        return ingresos.stream()
                .map(ingreso -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", ingreso.getId());
                    map.put("concepto", ingreso.getConcepto());
                    map.put("monto", ingreso.getMonto());
                    map.put("fecha", ingreso.getFecha());
                    map.put("mes", ingreso.getMes());
                    map.put("año", ingreso.getAño());
                    if (ingreso.getCategoria() != null) {
                        map.put("categoria", ingreso.getCategoria().getNombre());
                        map.put("color", ingreso.getCategoria().getColor());
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEgresosDetalladosNuevo(Long condominioId, Integer año, Integer mes,
            String periodo) {
        List<Egreso> egresos;

        if (año != null && mes != null) {
            egresos = egresoRepository.findByCondominioIdAndAñoAndMes(condominioId, año, mes);
        } else if (año != null) {
            egresos = egresoRepository.findByCondominioIdAndAño(condominioId, año);
        } else {
            egresos = egresoRepository.findByCondominioId(condominioId);
        }

        return egresos.stream()
                .map(egreso -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", egreso.getId());
                    map.put("concepto", egreso.getConcepto());
                    map.put("monto", egreso.getMonto());
                    map.put("fecha", egreso.getFecha());
                    map.put("mes", egreso.getMes());
                    map.put("año", egreso.getAño());
                    if (egreso.getCategoria() != null) {
                        map.put("categoria", egreso.getCategoria().getNombre());
                        map.put("color", egreso.getCategoria().getColor());
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMesesDisponibles(Long condominioId) {
        List<Saldo> todosSaldos = condominioId != null
                ? saldoRepository.findByUsuarioCondominioId(condominioId)
                : saldoRepository.findAll();

        Set<YearMonth> mesesSet = todosSaldos.stream()
                .filter(saldo -> saldo.getFechaLimite() != null)
                .map(saldo -> YearMonth.from(saldo.getFechaLimite()))
                .collect(Collectors.toSet());

        return mesesSet.stream()
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
                                    && s.getMonto().compareTo(BigDecimal.ZERO) > 0
                                    && s.getPagado())
                            .count();
                    long egresosCount = todosSaldos.stream()
                            .filter(s -> YearMonth.from(s.getFechaLimite()).equals(yearMonth)
                                    && s.getMonto().compareTo(BigDecimal.ZERO) > 0
                                    && !s.getPagado())
                            .count();

                    mesData.put("completo", ingresosCount > 0 && egresosCount > 0);
                    return mesData;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getIngresosDetallados(Long condominioId, LocalDate fechaInicio,
            LocalDate fechaFin) {
        List<Object[]> resultados = condominioId != null
                ? saldoRepository.findIngresosByCondominioAndFecha(condominioId, fechaInicio, fechaFin)
                : saldoRepository.findIngresosByFecha(fechaInicio, fechaFin);

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> ingreso = new HashMap<>();
                    ingreso.put("id", result[0]);
                    ingreso.put("concepto", result[1]);
                    ingreso.put("descripcion", result[2]);
                    ingreso.put("monto", result[3]);
                    ingreso.put("fecha", result[4]);

                    if (result[4] != null) {
                        LocalDate fecha = (LocalDate) result[4];
                        ingreso.put("mes", fecha.getMonthValue());
                        ingreso.put("año", fecha.getYear());
                    }

                    ingreso.put("usuario", result[5]);
                    ingreso.put("comprobante", result[6]);
                    return ingreso;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEgresosDetallados(Long condominioId, LocalDate fechaInicio,
            LocalDate fechaFin) {
        List<Object[]> resultados = condominioId != null
                ? saldoRepository.findEgresosByCondominioAndFecha(condominioId, fechaInicio, fechaFin)
                : saldoRepository.findEgresosByFecha(fechaInicio, fechaFin);

        return resultados.stream()
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
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getResumenMensual(Long condominioId, Integer año) {
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

        return resumenMensual;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEstadisticasGenerales(Long condominioId) {
        Object[] resultado = saldoRepository.findEstadisticasGenerales();

        Map<String, Object> estadisticas = new HashMap<>();

        if (resultado != null) {
            estadisticas.put("total", resultado[0]);
            estadisticas.put("pagados", resultado[1]);
            estadisticas.put("pendientes", resultado[2]);
            estadisticas.put("promedio", resultado[3]);
        }

        return estadisticas;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTop5Ingresos(Long condominioId) {
        List<Object[]> resultados = saldoRepository.findTop5Ingresos();

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> ingreso = new HashMap<>();
                    ingreso.put("concepto", result[0]);
                    ingreso.put("monto", result[1]);
                    ingreso.put("fecha", result[2]);
                    ingreso.put("usuario", result[3]);
                    return ingreso;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTop5Egresos(Long condominioId) {
        List<Object[]> resultados = saldoRepository.findTop5Egresos();

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> egreso = new HashMap<>();
                    egreso.put("concepto", result[0]);
                    egreso.put("monto", result[1]);
                    egreso.put("fecha", result[2]);
                    egreso.put("usuario", result[3]);
                    return egreso;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSaldosVencidos(Long condominioId) {
        List<Saldo> saldos = condominioId != null
                ? saldoRepository.findSaldosVencidosByCondominio(condominioId)
                : saldoRepository.findSaldosVencidos();

        return saldos.stream()
                .map(saldo -> {
                    Map<String, Object> saldoMap = new HashMap<>();
                    saldoMap.put("id", saldo.getId());
                    saldoMap.put("concepto", saldo.getConcepto());
                    saldoMap.put("descripcion", saldo.getDescripcion());
                    saldoMap.put("monto", saldo.getMonto());
                    saldoMap.put("fechaLimite", saldo.getFechaLimite());
                    saldoMap.put("diasVencido", LocalDate.now().toEpochDay() - saldo.getFechaLimite().toEpochDay());
                    saldoMap.put("usuario", saldo.getUsuario().getNombre());
                    return saldoMap;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getResumenPorAño() {
        List<Object[]> resultados = saldoRepository.findResumenPorAño();

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> resumen = new HashMap<>();
                    resumen.put("año", result[0]);
                    resumen.put("ingresos", result[1]);
                    resumen.put("egresos", result[2]);
                    resumen.put("countIngresos", result[3]);
                    resumen.put("countEgresos", result[4]);
                    return resumen;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTotalesPorMes(int año) {
        List<Object[]> resultados = saldoRepository.findTotalesPorMes(año);

        return resultados.stream()
                .map(result -> {
                    Map<String, Object> totalMes = new HashMap<>();
                    totalMes.put("mes", result[0]);
                    totalMes.put("ingresos", result[1]);
                    totalMes.put("egresos", result[2]);
                    return totalMes;
                })
                .collect(Collectors.toList());
    }
}