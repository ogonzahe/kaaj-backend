package com.kaaj.api.service;

import com.kaaj.api.dto.CrearReporteDTO;
import com.kaaj.api.model.Reporte;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReporteService {

    private final Map<Integer, List<Reporte>> store = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Reporte crearReporte(Integer usuarioId, CrearReporteDTO dto) {
        Reporte r = new Reporte();
        r.setId(seq.getAndIncrement()); // Lombok @Data te da setId
        r.setTitulo(dto.getTitulo());
        r.setDescripcion(dto.getDescripcion());
        r.setUbicacion(dto.getUbicacion());
        r.setImagenUrl(dto.getImagenUrl());
        r.setUsuarioId(usuarioId);
        r.setEstado("Pendiente");
        r.setCreadoEn(LocalDateTime.now());

        store.computeIfAbsent(usuarioId, k -> new ArrayList<>()).add(r);
        return r;
    }

    public List<Reporte> obtenerReportesPorUsuario(Integer usuarioId) {
        return store.getOrDefault(usuarioId, Collections.emptyList());
    }
}
