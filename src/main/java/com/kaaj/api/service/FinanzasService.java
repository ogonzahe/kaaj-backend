package com.kaaj.api.service;

import com.kaaj.api.dto.FinanzasDTO;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.PagoStripe;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.repository.SaldoRepository;
import com.kaaj.api.repository.PagoStripeRepository;
import com.kaaj.api.repository.CondominioRepository;
import com.kaaj.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanzasService {

    @Autowired
    private SaldoRepository saldoRepository;

    @Autowired
    private PagoStripeRepository pagoStripeRepository;

    @Autowired
    private CondominioRepository condominioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
}