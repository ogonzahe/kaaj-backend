package com.kaaj.api.controller;

import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import com.kaaj.api.service.StripeService;
import com.kaaj.api.dto.CrearPagoProgramadoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Autowired
    private PagoProgramadoRepository pagoProgramadoRepository;

    @Autowired
    private CondominioRepository condominioRepository;

    @Value("${stripe.api.public-key}")
    private String stripePublicKey;

    // ========== ENDPOINTS ADMINISTRATIVOS ==========

    @GetMapping("/admin/categorias-pagos")
    public ResponseEntity<?> obtenerCategoriasPagos() {
        try {
            List<String> categorias = Arrays.asList(
                    "Mantenimiento",
                    "Servicios Básicos",
                    "Cuota Extraordinaria",
                    "Amenidades",
                    "Seguridad",
                    "Administración",
                    "Otros");

            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/admin/categorias-pagos")
    public ResponseEntity<?> crearCategoriaPago(@RequestBody Map<String, String> request) {
        try {
            String nombre = request.get("nombre");
            if (nombre == null || nombre.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "El nombre de la categoría es requerido");
                return ResponseEntity.badRequest().body(error);
            }

            System.out.println("Nueva categoría creada: " + nombre);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría creada exitosamente");
            response.put("categoria", nombre);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/admin/pagos-programados")
    public ResponseEntity<?> obtenerPagosProgramadosAdmin(@RequestParam Integer condominioId) {
        try {
            System.out.println("Obteniendo pagos programados para condominio: " + condominioId);

            List<PagoProgramado> pagos = pagoProgramadoRepository.findByCondominioId(condominioId);

            List<Map<String, Object>> pagosFormateados = new ArrayList<>();
            for (PagoProgramado pago : pagos) {
                Map<String, Object> pagoMap = new HashMap<>();
                pagoMap.put("id", pago.getId());
                pagoMap.put("concepto", pago.getConcepto());
                pagoMap.put("categoria", pago.getCategoria());
                pagoMap.put("monto", pago.getMonto());
                pagoMap.put("fechaInicio", pago.getFechaInicio());
                pagoMap.put("fechaLimite", pago.getFechaLimite());
                pagoMap.put("esRecurrente", pago.getEsRecurrente());
                pagoMap.put("periodicidad", pago.getPeriodicidad());
                pagoMap.put("intervaloDias", pago.getIntervaloDias());
                pagoMap.put("descripcion", pago.getDescripcion());
                pagoMap.put("repeticiones", pago.getRepeticiones());

                // Contar usuarios asignados
                List<Integer> usuariosIds = pago.getUsuariosIds();
                int usuariosCount = usuariosIds != null ? usuariosIds.size() : 0;
                pagoMap.put("usuariosCount", usuariosCount);
                pagoMap.put("usuariosIds", usuariosIds != null ? usuariosIds : new ArrayList<>());
                pagoMap.put("apartamentosIds", usuariosIds != null ? usuariosIds : new ArrayList<>());

                // Contar saldos pendientes
                Long pendientesCount = saldoRepository.countByPagoProgramadoIdAndPendientes(pago.getId());
                Long pendientes = pendientesCount != null ? pendientesCount : 0L;
                pagoMap.put("saldosPendientes", pendientes);

                pagosFormateados.add(pagoMap);
            }

            return ResponseEntity.ok(pagosFormateados);
        } catch (Exception e) {
            System.out.println("Error al obtener pagos programados: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/admin/crear-pago-recurrente")
    public ResponseEntity<?> crearPagoRecurrente(@RequestBody CrearPagoProgramadoDTO request) {
        try {
            System.out.println("=== CREANDO PAGO RECURRENTE ===");
            System.out.println("Datos recibidos: " + request);

            // Validaciones básicas
            if (request.getConcepto() == null || request.getConcepto().trim().isEmpty()) {
                throw new RuntimeException("El concepto es requerido");
            }

            if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El monto debe ser mayor a cero");
            }

            if (request.getFechaInicio() == null) {
                throw new RuntimeException("La fecha de inicio es requerida");
            }

            if (request.getCondominioId() == null) {
                throw new RuntimeException("El condominio es requerido");
            }

            if (request.getApartamentosIds() == null || request.getApartamentosIds().isEmpty()) {
                throw new RuntimeException("Debe seleccionar al menos un usuario");
            }

            // Crear el pago programado
            PagoProgramado pagoProgramado = new PagoProgramado();
            pagoProgramado.setConcepto(request.getConcepto());
            pagoProgramado.setDescripcion(request.getDescripcion());
            pagoProgramado.setCategoria(request.getCategoria());
            pagoProgramado.setMonto(request.getMonto());
            pagoProgramado.setFechaInicio(request.getFechaInicio());
            pagoProgramado.setFechaLimite(request.getFechaLimite());
            pagoProgramado.setEsRecurrente(request.getEsRecurrente() != null ? request.getEsRecurrente() : false);
            pagoProgramado.setPeriodicidad(request.getPeriodicidad());
            pagoProgramado.setIntervaloDias(request.getIntervaloDias());
            pagoProgramado.setRepeticiones(request.getRepeticiones() != null ? request.getRepeticiones() : 1);
            pagoProgramado.setFechaCreacion(LocalDateTime.now());

            // Obtener condominio
            Condominio condominio = condominioRepository.findById(request.getCondominioId().intValue())
                    .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));
            pagoProgramado.setCondominio(condominio);

            // Convertir los IDs de apartamentos (que realmente son IDs de usuarios) a Integer
            List<Integer> usuariosIds = new ArrayList<>();
            for (Long id : request.getApartamentosIds()) {
                usuariosIds.add(id.intValue());
            }
            pagoProgramado.setUsuariosIds(usuariosIds);

            // Guardar pago programado
            PagoProgramado saved = pagoProgramadoRepository.save(pagoProgramado);
            System.out.println("PagoProgramado guardado con ID: " + saved.getId());

            // Generar saldos individuales para cada usuario
            int saldosCreados = generarSaldosParaUsuarios(saved, usuariosIds, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pago programado creado exitosamente");
            response.put("pagoProgramadoId", saved.getId());
            response.put("saldosCreados", saldosCreados);
            response.put("pagosGenerados", saldosCreados);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR al crear pago recurrente: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private int generarSaldosParaUsuarios(PagoProgramado pagoProgramado, List<Integer> usuariosIds, CrearPagoProgramadoDTO request) {
        int saldosCreados = 0;

        if (usuariosIds == null || usuariosIds.isEmpty()) {
            System.out.println("No hay usuarios para generar saldos");
            return 0;
        }

        System.out.println("Generando saldos para " + usuariosIds.size() + " usuarios");

        for (Integer usuarioId : usuariosIds) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                System.out.println("Usuario con ID " + usuarioId + " no encontrado");
                continue;
            }

            Usuario usuario = usuarioOpt.get();

            // Verificar que el usuario pertenece al condominio correcto
            if (usuario.getCondominio() == null ||
                !usuario.getCondominio().getId().equals(pagoProgramado.getCondominio().getId())) {
                System.out.println("Usuario " + usuario.getNombre() + " no pertenece al condominio");
                continue;
            }

            if (request.getEsRecurrente() != null && request.getEsRecurrente()) {
                // Crear pagos recurrentes
                int repeticiones = request.getRepeticiones() != null ? request.getRepeticiones() : 1;
                int intervaloDias = request.getIntervaloDias() != null ? request.getIntervaloDias() : 30;

                System.out.println("Creando " + repeticiones + " pagos recurrentes para usuario: " + usuario.getCorreo());

                for (int i = 0; i < repeticiones; i++) {
                    Saldo saldo = new Saldo();
                    saldo.setUsuario(usuario);
                    // CORREGIDO: Asignar condominio al saldo
                    saldo.setCondominio(usuario.getCondominio());
                    saldo.setConcepto(pagoProgramado.getConcepto() + " - " + (i + 1) + "/" + repeticiones);
                    saldo.setDescripcion(pagoProgramado.getDescripcion());
                    saldo.setCategoria(pagoProgramado.getCategoria());
                    saldo.setMonto(pagoProgramado.getMonto());
                    saldo.setSaldoActual(pagoProgramado.getMonto());
                    saldo.setPagado(false);
                    saldo.setEsRecurrente(true);
                    saldo.setNumeroRepeticion(i + 1);
                    saldo.setPagoProgramadoId(pagoProgramado.getId());

                    // Calcular fechas para cada repetición
                    LocalDate fechaPago = request.getFechaInicio().plusDays((long) intervaloDias * i);
                    LocalDate fechaLimite = fechaPago.plusDays(7); // 7 días para pagar

                    saldo.setFechaPago(fechaPago);
                    saldo.setFechaLimite(fechaLimite);
                    saldo.setTipoPago("RECURRENTE");
                    saldo.setUltimoMovimiento(new java.sql.Timestamp(System.currentTimeMillis()));

                    saldoRepository.save(saldo);
                    saldosCreados++;

                    System.out.println("Saldo recurrente creado: Repetición " + (i + 1) + " para " + usuario.getCorreo());
                }
            } else {
                // Crear pago único
                Saldo saldo = new Saldo();
                saldo.setUsuario(usuario);
                // CORREGIDO: Asignar condominio al saldo
                saldo.setCondominio(usuario.getCondominio());
                saldo.setConcepto(pagoProgramado.getConcepto());
                saldo.setDescripcion(pagoProgramado.getDescripcion());
                saldo.setCategoria(pagoProgramado.getCategoria());
                saldo.setMonto(pagoProgramado.getMonto());
                saldo.setSaldoActual(pagoProgramado.getMonto());
                saldo.setPagado(false);
                saldo.setEsRecurrente(false);
                saldo.setNumeroRepeticion(1);
                saldo.setPagoProgramadoId(pagoProgramado.getId());
                saldo.setFechaPago(pagoProgramado.getFechaInicio());
                saldo.setFechaLimite(pagoProgramado.getFechaLimite());
                saldo.setTipoPago("PROGRAMADO");
                saldo.setUltimoMovimiento(new java.sql.Timestamp(System.currentTimeMillis()));

                saldoRepository.save(saldo);
                saldosCreados++;

                System.out.println("Saldo único creado para " + usuario.getCorreo());
            }
        }

        System.out.println("Total saldos creados: " + saldosCreados);
        return saldosCreados;
    }

    @GetMapping("/admin/pagos-generados")
    public ResponseEntity<?> obtenerPagosGenerados(@RequestParam Integer condominioId) {
        try {
            System.out.println("Obteniendo pagos generados para condominio: " + condominioId);

            // Buscar todos los saldos del condominio
            List<Saldo> saldos = saldoRepository.findByCondominioId(condominioId);

            List<Map<String, Object>> pagosGenerados = new ArrayList<>();
            for (Saldo saldo : saldos) {
                Map<String, Object> pago = new HashMap<>();
                pago.put("id", saldo.getId());
                pago.put("concepto", saldo.getConcepto());
                pago.put("categoria", saldo.getCategoria());
                pago.put("monto", saldo.getMonto());
                pago.put("fechaLimite", saldo.getFechaLimite());
                pago.put("pagado", saldo.getPagado());
                pago.put("esRecurrente", saldo.isEsRecurrente());
                pago.put("numeroRepeticion", saldo.getNumeroRepeticion());
                pago.put("pagoProgramadoId", saldo.getPagoProgramadoId());

                // Información del usuario
                if (saldo.getUsuario() != null) {
                    pago.put("usuarioId", saldo.getUsuario().getId());
                    pago.put("usuarioNombre", saldo.getUsuario().getNombre());

                    // Obtener número de casa directamente del usuario
                    String numeroCasa = saldo.getUsuario().getNumeroCasa();
                    pago.put("numeroCasa", numeroCasa != null ? numeroCasa : "N/A");
                    pago.put("unidad", numeroCasa != null ? "Casa " + numeroCasa : "N/A");
                }

                pagosGenerados.add(pago);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pagosGenerados);
            response.put("total", pagosGenerados.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error al obtener pagos generados: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/admin/marcar-pagado/{saldoId}")
    public ResponseEntity<?> marcarPagoPagado(@PathVariable Integer saldoId) {
        try {
            System.out.println("Marcando saldo como pagado: " + saldoId);

            Saldo saldo = saldoRepository.findById(saldoId.longValue())
                    .orElseThrow(() -> new RuntimeException("Saldo no encontrado"));

            saldo.setPagado(true);
            saldo.setSaldoActual(BigDecimal.ZERO);
            saldo.setFechaPago(LocalDate.now());
            saldo.setFechaPagoCompletado(LocalDateTime.now());
            saldo.setUltimoMovimiento(new java.sql.Timestamp(System.currentTimeMillis()));

            saldoRepository.save(saldo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pago marcado como pagado exitosamente");
            response.put("saldoId", saldoId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error al marcar pago como pagado: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/admin/eliminar-pago/{pagoId}")
    public ResponseEntity<?> eliminarPagoProgramado(@PathVariable Integer pagoId) {
        try {
            System.out.println("Eliminando pago programado: " + pagoId);

            // 1. Eliminar todos los saldos asociados
            List<Saldo> saldosAsociados = saldoRepository.findByPagoProgramadoId(pagoId);
            if (!saldosAsociados.isEmpty()) {
                saldoRepository.deleteAll(saldosAsociados);
                System.out.println("Saldos eliminados: " + saldosAsociados.size());
            }

            // 2. Eliminar el pago programado
            pagoProgramadoRepository.deleteById(pagoId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pago programado eliminado exitosamente");
            response.put("pagoId", pagoId);
            response.put("saldosEliminados", saldosAsociados.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error al eliminar pago programado: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========== ENDPOINTS PARA USUARIOS ==========

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

            // Obtener condominio del usuario
            Condominio condominioUsuario = usuario.getCondominio();
            List<Saldo> saldosPendientes;

            if (condominioUsuario != null) {
                // Usar el nuevo método que filtra por usuario y condominio
                saldosPendientes = saldoRepository.findByUsuarioAndCondominioAndPagadoFalse(usuario, condominioUsuario);
                System.out.println("Buscando saldos para usuario " + usuario.getId() +
                                  " en condominio " + condominioUsuario.getId());
            } else {
                // Si no tiene condominio, usar el método antiguo
                saldosPendientes = saldoRepository.findSaldosPendientesByUsuario(usuario);
                System.out.println("Usuario sin condominio, usando método antiguo");
            }

            List<Map<String, Object>> saldos = new ArrayList<>();
            BigDecimal totalPendiente = BigDecimal.ZERO;

            for (Saldo saldo : saldosPendientes) {
                Map<String, Object> saldoMap = new HashMap<>();
                saldoMap.put("id", saldo.getId());
                saldoMap.put("concepto", saldo.getConcepto());
                saldoMap.put("descripcion", saldo.getDescripcion());
                saldoMap.put("categoria", saldo.getCategoria());

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
                saldoMap.put("esRecurrente", saldo.isEsRecurrente());
                saldoMap.put("numeroRepeticion", saldo.getNumeroRepeticion());
                saldoMap.put("pagoProgramadoId", saldo.getPagoProgramadoId());

                saldos.add(saldoMap);

                if (saldoActual != null) {
                    totalPendiente = totalPendiente.add(saldoActual);
                }
            }

            System.out.println("Total saldos encontrados para " + correo + ": " + saldos.size());
            System.out.println("Total pendiente: " + totalPendiente);

            Map<String, Object> response = new HashMap<>();
            response.put("saldos", saldos);
            response.put("totalPendiente", totalPendiente);
            response.put("success", true);
            response.put("condominioUsuario", condominioUsuario != null ? condominioUsuario.getId() : null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR al obtener saldos pendientes: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener saldos pendientes");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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

    @PostMapping("/confirmar-pago")
    public ResponseEntity<?> confirmarPago(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== CONFIRMANDO PAGO Y ACTUALIZANDO BD ===");

            String paymentIntentId = (String) request.get("paymentIntentId");
            String correo = (String) request.get("correo");
            BigDecimal monto = new BigDecimal(request.get("monto").toString());
            String descripcion = (String) request.get("descripcion");
            Integer saldoId = request.containsKey("saldoId") && request.get("saldoId") != null
                    ? Integer.parseInt(request.get("saldoId").toString())
                    : null;

            if (saldoId != null) {
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
                Saldo saldoEspecifico = saldoRepository.findById(saldoId.longValue())
                        .orElseThrow(() -> new RuntimeException("Saldo no encontrado con ID: " + saldoId));

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
                    saldoEspecifico.setFechaPagoCompletado(LocalDateTime.now());
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

                saldoEspecifico.setFechaPago(LocalDate.now());
                saldoEspecifico.setUltimoMovimiento(new java.sql.Timestamp(System.currentTimeMillis()));
                saldosActualizados.add(saldoEspecifico);

            } else {
                System.out.println("Procesando pago general del total pendiente");
                pagoStripe.setEsParcial(false);

                // Obtener saldos pendientes del usuario
                Condominio condominioUsuario = usuario.getCondominio();
                List<Saldo> saldosPendientes;

                if (condominioUsuario != null) {
                    saldosPendientes = saldoRepository.findByUsuarioAndCondominioAndPagadoFalse(usuario, condominioUsuario);
                } else {
                    saldosPendientes = saldoRepository.findSaldosPendientesByUsuario(usuario);
                }

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
                            saldo.setFechaPago(LocalDate.now());
                            saldo.setFechaPagoCompletado(LocalDateTime.now());
                            saldoRestante = saldoRestante.subtract(saldoPendiente);
                            System.out.println("Saldo " + saldo.getId() + " PAGADO COMPLETAMENTE");
                        } else {
                            saldo.setSaldoActual(saldoPendiente.subtract(saldoRestante));
                            saldo.setPagado(false);
                            saldo.setFechaPago(LocalDate.now());
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

    @PostMapping("/admin/crear-pago")
    public ResponseEntity<?> crearPagoProgramadoAdmin(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== ADMIN CREANDO PAGO PROGRAMADO ===");
            System.out.println("Datos recibidos: " + request);

            // Usar el nuevo DTO para parsear
            CrearPagoProgramadoDTO dto = new CrearPagoProgramadoDTO();
            dto.setConcepto((String) request.get("concepto"));
            dto.setDescripcion((String) request.get("descripcion"));
            dto.setCategoria((String) request.get("categoria"));

            Object montoObj = request.get("monto");
            if (montoObj instanceof Number) {
                dto.setMonto(BigDecimal.valueOf(((Number) montoObj).doubleValue()));
            } else if (montoObj != null) {
                dto.setMonto(new BigDecimal(montoObj.toString()));
            }

            Object fechaInicioObj = request.get("fechaInicio");
            if (fechaInicioObj != null) {
                dto.setFechaInicio(LocalDate.parse(fechaInicioObj.toString()));
            }

            Object fechaLimiteObj = request.get("fechaLimite");
            if (fechaLimiteObj != null) {
                dto.setFechaLimite(LocalDate.parse(fechaLimiteObj.toString()));
            }

            @SuppressWarnings("unchecked")
            List<Integer> usuariosIdsInt = (List<Integer>) request.get("apartamentosIds");
            if (usuariosIdsInt != null) {
                List<Long> usuariosIdsLong = new ArrayList<>();
                for (Integer id : usuariosIdsInt) {
                    usuariosIdsLong.add(id.longValue());
                }
                dto.setApartamentosIds(usuariosIdsLong);
            }

            Object esRecurrenteObj = request.get("esRecurrente");
            if (esRecurrenteObj != null) {
                dto.setEsRecurrente(Boolean.parseBoolean(esRecurrenteObj.toString()));
            }

            Object condominioIdObj = request.get("condominioId");
            if (condominioIdObj != null) {
                dto.setCondominioId(Long.parseLong(condominioIdObj.toString()));
            }

            Object repeticionesObj = request.get("repeticiones");
            if (repeticionesObj != null) {
                dto.setRepeticiones(Integer.parseInt(repeticionesObj.toString()));
            }

            // Llamar al nuevo método con el DTO
            return crearPagoRecurrente(dto);

        } catch (Exception e) {
            System.out.println("ERROR al crear pago programado: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());
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