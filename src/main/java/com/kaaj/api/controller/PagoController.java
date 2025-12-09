package com.kaaj.api.controller;

import com.kaaj.api.model.PagoStripe;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.repository.PagoStripeRepository;
import com.kaaj.api.repository.SaldoRepository;
import com.kaaj.api.repository.UsuarioRepository;
import com.kaaj.api.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SaldoRepository saldoRepository;

    @Autowired
    private PagoStripeRepository pagoStripeRepository;

    @Value("${stripe.api.public-key}")
    private String stripePublicKey;

    @GetMapping("/config-stripe")
    public ResponseEntity<?> getStripeConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("publicKey", stripePublicKey);
            config.put("status", "ready");
            config.put("currency", "MXN");
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/crear-intento-pago")
    public ResponseEntity<?> crearIntentoPago(@RequestBody Map<String, Object> request) {
        try {
            String correo = (String) request.get("correo");
            BigDecimal monto = new BigDecimal(request.get("monto").toString());
            String descripcion = (String) request.get("descripcion");

            if (stripeService == null) {
                throw new RuntimeException("StripeService no está disponible");
            }

            String clientSecret = stripeService.crearPaymentIntent(monto, descripcion, correo);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", clientSecret);
            response.put("publicKey", stripePublicKey);
            response.put("status", "success");
            response.put("message", "PaymentIntent creado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al crear intento de pago");
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/saldos-pendientes")
    public ResponseEntity<?> getSaldosPendientes(@RequestParam String correo) {
        try {
            Usuario usuario = usuarioRepository.findByCorreo(correo);
            if (usuario == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            List<Saldo> saldosPendientes = saldoRepository.findSaldosPendientesByUsuario(usuario);

            List<Map<String, Object>> saldos = new ArrayList<>();
            BigDecimal totalPendiente = BigDecimal.ZERO;

            for (Saldo saldo : saldosPendientes) {
                Map<String, Object> saldoMap = new HashMap<>();
                saldoMap.put("id", saldo.getId());
                saldoMap.put("concepto", saldo.getConcepto());
                saldoMap.put("descripcion", saldo.getDescripcion());

                if (saldo.getFechaLimite() != null) {
                    saldoMap.put("fechaLimite", saldo.getFechaLimite().toString());
                } else {
                    saldoMap.put("fechaLimite", null);
                }

                BigDecimal saldoActual = saldo.getSaldoActual();
                if (saldoActual == null) {
                    saldoActual = saldo.getMonto();
                }
                saldoMap.put("saldoActual", saldoActual);

                saldos.add(saldoMap);

                if (saldoActual != null) {
                    totalPendiente = totalPendiente.add(saldoActual);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("saldos", saldos);
            response.put("totalPendiente", totalPendiente);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener saldos pendientes");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/confirmar-pago")
    public ResponseEntity<?> confirmarPago(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== CONFIRMANDO PAGO Y ACTUALIZANDO BD ===");

            String paymentIntentId = (String) request.get("paymentIntentId");
            String correo = (String) request.get("correo");
            BigDecimal monto = new BigDecimal(request.get("monto").toString());
            String descripcion = (String) request.get("descripcion");
            Integer saldoId = null;

            if (request.containsKey("saldoId") && request.get("saldoId") != null) {
                saldoId = Integer.parseInt(request.get("saldoId").toString());
                System.out.println("Pago específico para saldo ID: " + saldoId);
            }

            System.out.println("PaymentIntent ID: " + paymentIntentId);
            System.out.println("Correo: " + correo);
            System.out.println("Monto: " + monto);
            System.out.println("Descripción: " + descripcion);

            Usuario usuario = usuarioRepository.findByCorreo(correo);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            PagoStripe pagoStripe = new PagoStripe();
            pagoStripe.setUsuario(usuario);
            pagoStripe.setStripePaymentId(paymentIntentId);
            pagoStripe.setMonto(monto);
            pagoStripe.setMoneda("MXN");
            pagoStripe.setConcepto(descripcion);
            pagoStripe.setEstado("succeeded");
            pagoStripe.setFechaCreacion(LocalDateTime.now());
            pagoStripe.setFechaConfirmacion(LocalDateTime.now());

            BigDecimal saldoRestante = monto;
            List<Saldo> saldosActualizados = new ArrayList<>();

            if (saldoId != null) {
                System.out.println("Procesando pago específico para saldo ID: " + saldoId);

                // Buscar saldo por ID
                Saldo saldoEspecifico = saldoRepository.findById(saldoId).orElse(null);

                if (saldoEspecifico == null) {
                    throw new RuntimeException("Saldo no encontrado con ID: " + saldoId);
                }

                // Verificar que el saldo pertenece al usuario
                if (!saldoEspecifico.getUsuario().getId().equals(usuario.getId())) {
                    throw new RuntimeException("El saldo no pertenece al usuario");
                }

                BigDecimal saldoPendiente = saldoEspecifico.getSaldoActual();
                if (saldoPendiente == null) {
                    saldoPendiente = saldoEspecifico.getMonto();
                }

                System.out.println("Saldo pendiente del concepto: " + saldoPendiente);

                if (monto.compareTo(saldoPendiente) >= 0) {
                    saldoEspecifico.setPagado(true);
                    saldoEspecifico.setSaldoActual(BigDecimal.ZERO);
                    saldoRestante = monto.subtract(saldoPendiente);
                    pagoStripe.setEsParcial(false);
                    System.out.println("Pago COMPLETO del saldo ID: " + saldoId);
                } else {
                    saldoEspecifico.setSaldoActual(saldoPendiente.subtract(monto));
                    saldoEspecifico.setPagado(false);
                    saldoRestante = BigDecimal.ZERO;
                    pagoStripe.setEsParcial(true);
                    System.out.println("Pago PARCIAL del saldo ID: " + saldoId +
                                     ". Nuevo saldo: " + saldoEspecifico.getSaldoActual());
                }

                saldoEspecifico.setFechaPago(java.time.LocalDate.now());
                saldoEspecifico.setUltimoMovimiento(new java.sql.Timestamp(System.currentTimeMillis()));
                saldosActualizados.add(saldoEspecifico);

            } else {
                System.out.println("Procesando pago general del total pendiente");
                pagoStripe.setEsParcial(false);

                List<Saldo> saldosPendientes = saldoRepository.findSaldosPendientesByUsuario(usuario);
                System.out.println("Saldos pendientes encontrados: " + saldosPendientes.size());

                for (Saldo saldo : saldosPendientes) {
                    if (saldoRestante.compareTo(BigDecimal.ZERO) <= 0) {
                        break;
                    }

                    BigDecimal saldoPendiente = saldo.getSaldoActual();
                    if (saldoPendiente == null) {
                        saldoPendiente = saldo.getMonto();
                    }

                    if (saldoPendiente != null && saldoPendiente.compareTo(BigDecimal.ZERO) > 0) {
                        System.out.println("Procesando saldo ID: " + saldo.getId() +
                                         ", Concepto: " + saldo.getConcepto() +
                                         ", Monto: " + saldoPendiente);

                        if (saldoPendiente.compareTo(saldoRestante) <= 0) {
                            saldo.setPagado(true);
                            saldo.setSaldoActual(BigDecimal.ZERO);
                            saldo.setFechaPago(java.time.LocalDate.now());
                            saldoRestante = saldoRestante.subtract(saldoPendiente);
                            System.out.println("Saldo " + saldo.getId() + " PAGADO COMPLETAMENTE");
                        } else {
                            saldo.setSaldoActual(saldoPendiente.subtract(saldoRestante));
                            saldo.setPagado(false);
                            saldo.setFechaPago(java.time.LocalDate.now());
                            pagoStripe.setEsParcial(true);
                            saldoRestante = BigDecimal.ZERO;
                            System.out.println("Saldo " + saldo.getId() + " PAGADO PARCIALMENTE. Nuevo saldo: " +
                                             saldo.getSaldoActual());
                        }

                        saldo.setUltimoMovimiento(new java.sql.Timestamp(System.currentTimeMillis()));
                        saldosActualizados.add(saldo);
                    }
                }
            }

            pagoStripeRepository.save(pagoStripe);
            System.out.println("Pago registrado en BD con ID: " + pagoStripe.getId());

            if (!saldosActualizados.isEmpty()) {
                saldoRepository.saveAll(saldosActualizados);
                System.out.println("Saldos actualizados: " + saldosActualizados.size());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pago confirmado y registrado exitosamente");
            response.put("paymentIntentId", paymentIntentId);
            response.put("fecha", LocalDateTime.now().toString());
            response.put("montoPagado", monto);
            response.put("saldosActualizados", saldosActualizados.size());
            response.put("saldoRestante", saldoRestante);
            response.put("esParcial", pagoStripe.getEsParcial());

            System.out.println("=== PAGO CONFIRMADO Y BD ACTUALIZADA ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR al confirmar pago: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al confirmar el pago: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "StripeService " + (stripeService != null ? "disponible" : "NO disponible"));
        response.put("publicKey", stripePublicKey != null ? "Configurada" : "No configurada");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}