package com.kaaj.api.controller;

import com.kaaj.api.dto.VisitaRequest;
import com.kaaj.api.dto.VisitaResponse;
import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visitas")
public class VisitaController {

    private final VisitaRepository visitaRepo;
    private final EscaneoQrRepository escaneoQrRepo;
    private final UsuarioRepository usuarioRepo;

    // Generar nueva visita con QR
    @PostMapping("/generar")
    public ResponseEntity<?> generarVisita(@RequestBody VisitaRequest request) {
        try {
            // Generar c??digo QR ??nico
            String codigoQr = "KAJ-VIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Visita visita = new Visita();
            visita.setCodigoQr(codigoQr);
            visita.setNombreVisitante(request.getNombreVisitante());
            visita.setApellidoVisitado(request.getApellidoVisitado());
            visita.setCondominio(request.getCondominio());
            visita.setTipoAcceso(Visita.TipoAcceso.valueOf(request.getTipoAcceso()));
            visita.setFechaProgramada(request.getFechaProgramada());
            visita.setHoraProgramada(request.getHoraProgramada());
            visita.setEstado(Visita.EstadoVisita.Generado);

            // Asociar usuario si se proporciona
            if (request.getUsuarioId() != null) {
                Usuario usuario = usuarioRepo.findById(request.getUsuarioId()).orElse(null);
                visita.setUsuario(usuario);
            }

            Visita savedVisita = visitaRepo.save(visita);

            // Crear respuesta
            VisitaResponse response = mapToResponse(savedVisita);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al generar visita", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar visita");
        }
    }

    // Escanear QR y registrar uso
    @PostMapping("/escanear/{codigoQr}")
    public ResponseEntity<?> escanearQr(@PathVariable String codigoQr) {
        try {
            Optional<Visita> visitaOpt = visitaRepo.findByCodigoQr(codigoQr);

            if (visitaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("QR no encontrado");
            }

            Visita visita = visitaOpt.get();

            // Verificar si ya fue utilizado
            if (visita.getEstado() == Visita.EstadoVisita.Utilizado) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este QR ya fue utilizado");
            }

            // Verificar si est?? expirado (fecha programada pasada)
            if (visita.getFechaProgramada().isBefore(java.time.LocalDate.now())) {
                visita.setEstado(Visita.EstadoVisita.Expirado);
                visitaRepo.save(visita);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("QR expirado");
            }

            // Marcar como utilizado
            visita.setEstado(Visita.EstadoVisita.Utilizado);
            visita.setFechaUtilizacion(LocalDateTime.now());
            visitaRepo.save(visita);

            // Registrar escaneo
            EscaneoQr escaneo = new EscaneoQr();
            escaneo.setVisita(visita);
            escaneo.setDispositivo("Sistema de Acceso");
            escaneo.setUbicacion(visita.getCondominio());
            escaneoQrRepo.save(escaneo);

            VisitaResponse response = mapToResponse(visita);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al escanear QR: {}", codigoQr, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al escanear QR");
        }
    }

    // Obtener visitas por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getVisitasPorUsuario(@PathVariable Integer usuarioId) {
        try {
            List<Visita> visitas = visitaRepo.findByUsuarioIdOrderByCreadoEnDesc(usuarioId);
            List<VisitaResponse> responses = visitas.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error al obtener visitas por usuario con id: {}", usuarioId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener visitas");
        }
    }

    // Obtener todas las visitas (para administraci??n)
    @GetMapping
    public ResponseEntity<?> getAllVisitas() {
        try {
            List<Visita> visitas = visitaRepo.findAll();
            List<VisitaResponse> responses = visitas.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error al obtener todas las visitas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener visitas");
        }
    }

    // Obtener estad??sticas de visitas
    @GetMapping("/estadisticas")
    public ResponseEntity<?> getEstadisticas() {
        try {
            List<Visita> todasVisitas = visitaRepo.findAll();

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("total", todasVisitas.size());
            estadisticas.put("generados", todasVisitas.stream()
                    .filter(v -> v.getEstado() == Visita.EstadoVisita.Generado).count());
            estadisticas.put("utilizados", todasVisitas.stream()
                    .filter(v -> v.getEstado() == Visita.EstadoVisita.Utilizado).count());
            estadisticas.put("expirados", todasVisitas.stream()
                    .filter(v -> v.getEstado() == Visita.EstadoVisita.Expirado).count());

            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error("Error al obtener estadisticas de visitas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener estadisticas");
        }
    }

    // M??todo auxiliar para mapear entidad a DTO
    private VisitaResponse mapToResponse(Visita visita) {
        VisitaResponse response = new VisitaResponse();
        response.setId(visita.getId());
        response.setCodigoQr(visita.getCodigoQr());
        response.setNombreVisitante(visita.getNombreVisitante());
        response.setApellidoVisitado(visita.getApellidoVisitado());
        response.setCondominio(visita.getCondominio());
        response.setTipoAcceso(visita.getTipoAcceso().name());
        response.setFechaProgramada(visita.getFechaProgramada());
        response.setHoraProgramada(visita.getHoraProgramada());
        response.setEstado(visita.getEstado().name());
        response.setFechaUtilizacion(visita.getFechaUtilizacion());
        response.setCreadoEn(visita.getCreadoEn());
        return response;
    }
}