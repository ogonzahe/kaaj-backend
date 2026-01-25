package com.kaaj.api.controller;

import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/api/seguridad")
public class SeguridadController {

    @Autowired
    private VisitaRepository visitaRepo;

    @Autowired
    private EscaneoQrRepository escaneoQrRepo;

    @Autowired
    private AccesoSeguridadRepository accesoSeguridadRepo;

    // Obtener visitas programadas para hoy
    @GetMapping("/visitas/hoy")
    public ResponseEntity<?> getVisitasHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            List<Visita> visitas = visitaRepo.findByFechaProgramada(hoy);

            if (visitas == null) {
                visitas = new ArrayList<>();
            }

            List<Map<String, Object>> visitasDto = new ArrayList<>();

            for (Visita visita : visitas) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", visita.getId());
                dto.put("codigoQr", visita.getCodigoQr());
                dto.put("nombreVisitante", visita.getNombreVisitante());
                dto.put("apellidoVisitado", visita.getApellidoVisitado());
                dto.put("condominio", visita.getCondominio());
                dto.put("tipoAcceso", visita.getTipoAcceso().name());
                dto.put("fechaProgramada", visita.getFechaProgramada().toString());
                dto.put("horaProgramada", visita.getHoraProgramada() != null ?
                    visita.getHoraProgramada().toString().substring(0, 5) : "00:00");
                dto.put("estado", visita.getEstado().name());
                visitasDto.add(dto);
            }

            return ResponseEntity.ok(visitasDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener visitas: " + e.getMessage()));
        }
    }

    // Registrar acceso desde QR escaneado
    @PostMapping("/registrar-acceso")
    public ResponseEntity<?> registrarAcceso(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== REGISTRANDO ACCESO ===");

            String qrTexto = (String) request.get("qrTexto");
            Integer guardiaId = (Integer) request.get("guardiaId");

            if (qrTexto == null || qrTexto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Texto QR vacío"));
            }

            // Extraer código del QR
            String codigoQr = extraerCodigoQR(qrTexto);

            System.out.println("Código QR extraído: " + codigoQr);

            if (codigoQr == null || codigoQr.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Formato de QR inválido"));
            }

            // Buscar visita por código QR
            Optional<Visita> visitaOpt = visitaRepo.findByCodigoQr(codigoQr);

            if (visitaOpt.isEmpty()) {
                System.out.println("QR no encontrado: " + codigoQr);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "QR no encontrado en el sistema"));
            }

            Visita visita = visitaOpt.get();
            System.out.println("Visita encontrada: " + visita.getNombreVisitante() +
                             " | Fecha: " + visita.getFechaProgramada() +
                             " | Hora: " + visita.getHoraProgramada());

            LocalDate hoy = LocalDate.now();
            LocalTime ahora = LocalTime.now();
            LocalDate fechaVisita = visita.getFechaProgramada();
            LocalTime horaVisita = visita.getHoraProgramada();

            // 1. Verificar si ya fue utilizado
            if (visita.getEstado() == Visita.EstadoVisita.Utilizado) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "⚠️ Este QR ya fue utilizado"));
            }

            // 2. Verificar si está expirado (fecha pasada)
            if (fechaVisita.isBefore(hoy)) {
                visita.setEstado(Visita.EstadoVisita.Expirado);
                visitaRepo.save(visita);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "❌ QR expirado (fecha pasada)"));
            }

            // 3. Si es para hoy, validar hora
            if (fechaVisita.equals(hoy)) {
                if (horaVisita != null) {
                    LocalTime horaMinima = horaVisita.minusHours(2);
                    LocalTime horaMaxima = horaVisita.plusHours(4);

                    if (ahora.isBefore(horaMinima)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of(
                                "success", false,
                                "message", "⏳ Visita programada para " + horaVisita +
                                         ". Puede acceder desde " + horaMinima
                            ));
                    }

                    if (ahora.isAfter(horaMaxima)) {
                        visita.setEstado(Visita.EstadoVisita.Expirado);
                        visitaRepo.save(visita);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of(
                                "success", false,
                                "message", "⌛️ La hora de acceso ha expirado. Hora programada: " + horaVisita
                            ));
                    }
                }

                System.out.println("✅ QR válido para hoy - Hora dentro del rango permitido");
            }

            // 4. Si es para fecha futura
            if (fechaVisita.isAfter(hoy)) {
                System.out.println("📅 QR para fecha futura: " + fechaVisita);
                // Permitir QR futuros
            }

            // ========== REGISTRAR ACCESO ==========

            // Actualizar estado de la visita
            visita.setEstado(Visita.EstadoVisita.Utilizado);
            visita.setFechaUtilizacion(LocalDateTime.now());
            visitaRepo.save(visita);
            System.out.println("✅ Estado actualizado a Utilizado");

            // Registrar en EscaneoQr
            EscaneoQr escaneo = new EscaneoQr();
            escaneo.setVisita(visita);
            escaneo.setDispositivo("Panel Seguridad Web");
            escaneo.setUbicacion(visita.getCondominio());
            escaneo.setFechaEscaneo(LocalDateTime.now());
            escaneoQrRepo.save(escaneo);
            System.out.println("✅ Escaneo registrado en BD - ID: " + escaneo.getId());

            // Mensaje según fecha
            String mensajeExito;
            if (fechaVisita.equals(hoy)) {
                mensajeExito = "✅ Acceso registrado para visita de hoy";
            } else if (fechaVisita.isAfter(hoy)) {
                long diasFaltantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaVisita);
                mensajeExito = "✅ Acceso registrado para visita programada en " + diasFaltantes + " días";
            } else {
                mensajeExito = "✅ Acceso registrado exitosamente";
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", mensajeExito,
                "visita", Map.of(
                    "id", visita.getId(),
                    "visitante", visita.getNombreVisitante(),
                    "residente", visita.getApellidoVisitado(),
                    "condominio", visita.getCondominio(),
                    "tipo", visita.getTipoAcceso().name(),
                    "fechaProgramada", visita.getFechaProgramada().toString(),
                    "horaProgramada", visita.getHoraProgramada() != null ?
                        visita.getHoraProgramada().toString().substring(0, 5) : "",
                    "fechaHoraAcceso", LocalDateTime.now().toString(),
                    "codigoQr", visita.getCodigoQr()
                )
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error interno: " + e.getMessage()));
        }
    }

    // Método para extraer código del texto QR
    private String extraerCodigoQR(String qrTexto) {
        if (qrTexto == null || qrTexto.isEmpty()) {
            return null;
        }

        try {
            // Buscar línea que contiene "CODIGO:"
            String[] lines = qrTexto.split("\n");
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("CODIGO:")) {
                    return trimmedLine.substring("CODIGO:".length()).trim();
                }
            }

            // Si no encuentra "CODIGO:", buscar cualquier línea que contenga "KAJ-"
            for (String line : lines) {
                if (line.contains("KAJ-")) {
                    String[] parts = line.split("KAJ-");
                    if (parts.length > 1) {
                        return "KAJ-" + parts[1].trim();
                    }
                }
            }

            // Si el texto ya es un código
            return qrTexto.trim();

        } catch (Exception e) {
            System.out.println("Error extrayendo código QR: " + e.getMessage());
        }

        return qrTexto.trim();
    }

    // Obtener historial de escaneos
    @GetMapping("/historial")
    public ResponseEntity<?> getHistorial() {
        try {
            List<EscaneoQr> escaneos = escaneoQrRepo.findAllByOrderByFechaEscaneoDesc();

            List<Map<String, Object>> historial = new ArrayList<>();
            int limite = Math.min(escaneos.size(), 10);

            for (int i = 0; i < limite; i++) {
                EscaneoQr escaneo = escaneos.get(i);
                Map<String, Object> item = new HashMap<>();
                item.put("id", escaneo.getId());
                item.put("visitante", escaneo.getVisita().getNombreVisitante());
                item.put("residente", escaneo.getVisita().getApellidoVisitado());
                item.put("condominio", escaneo.getVisita().getCondominio());
                item.put("fechaHora", escaneo.getFechaEscaneo());
                item.put("dispositivo", escaneo.getDispositivo());
                historial.add(item);
            }

            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    // Obtener estadísticas del día
    @GetMapping("/estadisticas-hoy")
    public ResponseEntity<?> getEstadisticasHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            List<Visita> visitasHoy = visitaRepo.findByFechaProgramada(hoy);

            if (visitasHoy == null) {
                visitasHoy = new ArrayList<>();
            }

            long total = visitasHoy.size();
            long utilizados = visitasHoy.stream()
                .filter(v -> v.getEstado() == Visita.EstadoVisita.Utilizado)
                .count();
            long pendientes = visitasHoy.stream()
                .filter(v -> v.getEstado() == Visita.EstadoVisita.Generado)
                .count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("utilizados", utilizados);
            stats.put("pendientes", pendientes);
            stats.put("porcentaje", total > 0 ? (utilizados * 100 / total) : 0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
}