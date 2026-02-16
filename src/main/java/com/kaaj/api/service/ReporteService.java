package com.kaaj.api.service;

import com.kaaj.api.dto.CrearReporteDTO;
import com.kaaj.api.model.Reporte;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.repository.ReporteRepository;
import com.kaaj.api.repository.UsuarioRepository;
import com.kaaj.api.repository.CondominioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CondominioRepository condominioRepository;

    @Transactional
    public Reporte crearReporte(CrearReporteDTO crearReporteDTO) {
        Usuario usuario = usuarioRepository.findById(crearReporteDTO.getUsuarioId().intValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Condominio condominio = condominioRepository.findById(crearReporteDTO.getCondominioId().intValue())
                .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));

        Reporte reporte = new Reporte();
        reporte.setTitulo(crearReporteDTO.getTitulo());
        reporte.setDescripcion(crearReporteDTO.getDescripcion());
        reporte.setUbicacion(crearReporteDTO.getUbicacion());
        reporte.setTipo(crearReporteDTO.getTipo());
        reporte.setUsuario(usuario);
        reporte.setCondominio(condominio);
        reporte.setEstado("pendiente");

        return reporteRepository.save(reporte);
    }

    @Transactional(readOnly = true)
    public List<Reporte> obtenerReportesPorUsuario(Long usuarioId) {
        return reporteRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Reporte> obtenerReportesPorCondominio(Long condominioId) {
        return reporteRepository.findByCondominioId(condominioId);
    }

    @Transactional
    public Reporte actualizarEstado(Long reporteId, String estado) {
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        reporte.setEstado(estado);
        return reporteRepository.save(reporte);
    }
}