package com.kaaj.api.job;

import com.kaaj.api.model.PagoProgramado;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Apartamento;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.repository.PagoProgramadoRepository;
import com.kaaj.api.repository.SaldoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PagoRecurrenteJob {

    @Autowired
    private PagoProgramadoRepository pagoProgramadoRepository;

    @Autowired
    private SaldoRepository saldoRepository;

    @Scheduled(cron = "0 0 8 * * ?") // Ejecutar todos los días a las 8 AM
    @Transactional
    public void generarPagosRecurrentes() {
        System.out.println("=== EJECUTANDO JOB DE PAGOS RECURRENTES ===");
        System.out.println("Fecha actual: " + LocalDate.now());

        List<PagoProgramado> pagosRecurrentes = pagoProgramadoRepository.findByEsRecurrenteTrue();
        System.out.println("Pagos recurrentes encontrados: " + pagosRecurrentes.size());

        for (PagoProgramado pago : pagosRecurrentes) {
            try {
                generarPagosParaPagoRecurrente(pago);
            } catch (Exception e) {
                System.out.println("Error procesando pago recurrente ID " + pago.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("=== JOB DE PAGOS RECURRENTES COMPLETADO ===");
    }

    private void generarPagosParaPagoRecurrente(PagoProgramado pago) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio = pago.getFechaInicio();
        Integer intervaloDias = pago.getIntervaloDias() != null ? pago.getIntervaloDias() : 30;

        // Calcular el número de repetición actual
        long diasDesdeInicio = hoy.toEpochDay() - fechaInicio.toEpochDay();

        // Solo procesar si estamos en un día que es múltiplo del intervalo
        if (diasDesdeInicio >= 0 && diasDesdeInicio % intervaloDias == 0) {
            int numeroRepeticion = (int) (diasDesdeInicio / intervaloDias) + 1;

            System.out.println("Generando pago recurrente: " + pago.getConcepto() +
                    ", Repetición #" + numeroRepeticion);

            // Verificar si ya se generó esta repetición
            boolean yaGenerado = false;
            if (pago.getApartamentos() != null) {
                for (Apartamento apartamento : pago.getApartamentos()) {
                    if (apartamento.getPropietario() != null) {
                        List<Saldo> saldosExistentes = saldoRepository
                                .findByUsuarioAndPagoProgramadoIdAndNumeroRepeticion(
                                        apartamento.getPropietario(), pago.getId(), numeroRepeticion);
                        if (!saldosExistentes.isEmpty()) {
                            yaGenerado = true;
                            break;
                        }
                    }
                }
            }

            if (!yaGenerado && pago.getApartamentos() != null) {
                // Generar nuevos saldos para esta repetición
                int nuevosSaldos = 0;
                for (Apartamento apartamento : pago.getApartamentos()) {
                    Usuario usuario = apartamento.getPropietario();
                    if (usuario == null)
                        continue;

                    Saldo saldo = new Saldo();
                    saldo.setUsuario(usuario);
                    saldo.setConcepto(pago.getConcepto() + " - " + numeroRepeticion);
                    saldo.setDescripcion(pago.getDescripcion());
                    saldo.setCategoria(pago.getCategoria());
                    saldo.setMonto(pago.getMonto());
                    saldo.setSaldoActual(pago.getMonto());
                    saldo.setPagado(false);
                    saldo.setEsRecurrente(true);
                    saldo.setNumeroRepeticion(numeroRepeticion);
                    saldo.setPagoProgramadoId(pago.getId());
                    saldo.setFechaPago(hoy);
                    saldo.setFechaLimite(hoy.plusDays(7)); // 7 días para pagar
                    saldo.setTipoPago("RECURRENTE");
                    saldo.setUltimoMovimiento(new java.sql.Timestamp(System.currentTimeMillis()));

                    saldoRepository.save(saldo);
                    nuevosSaldos++;
                }

                System.out.println("Generados " + nuevosSaldos + " nuevos saldos para repetición #" + numeroRepeticion);
            } else if (yaGenerado) {
                System.out.println("La repetición #" + numeroRepeticion + " ya fue generada anteriormente");
            }
        }
    }
}