package com.kaaj.api.service;

import com.kaaj.api.dto.MantenimientoDTO;
import com.kaaj.api.dto.ReporteMantenimientoDTO;
import com.kaaj.api.model.MantenimientoEntity;
import com.kaaj.api.model.TipoMantenimiento;
import com.kaaj.api.model.EstatusMantenimientoEntity;
import com.kaaj.api.repository.MantenimientoRepository;
import com.kaaj.api.repository.TipoMantenimientoRepository;
import com.kaaj.api.repository.EstatusMantenimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MantenimientoService {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private TipoMantenimientoRepository tipoMantenimientoRepository;

    @Autowired
    private EstatusMantenimientoRepository estatusMantenimientoRepository;

    public MantenimientoEntity crearMantenimiento(ReporteMantenimientoDTO reporteDTO) {
        MantenimientoEntity mantenimiento = new MantenimientoEntity();

        // Configurar datos básicos
        mantenimiento.setTituloReporte(reporteDTO.getTituloReporte());
        mantenimiento.setMensaje(reporteDTO.getDescripcion());
        mantenimiento.setUsuarioApartamento(reporteDTO.getUsuario() + " - Casa " + reporteDTO.getNumeroCasa());
        mantenimiento.setFechaAlta(new Date());

        // Configurar condominio y usuario
        mantenimiento.setCondominioId(reporteDTO.getCondominioId());
        mantenimiento.setUsuarioId(reporteDTO.getUsuarioId());
        mantenimiento.setUbicacion(reporteDTO.getUbicacion());
        mantenimiento.setNumeroCasa(reporteDTO.getNumeroCasa());

        // Configurar tipo
        TipoMantenimiento tipo = tipoMantenimientoRepository.findById(reporteDTO.getIdTipo())
                .orElseGet(() -> {
                    // Si no existe, crear uno por defecto
                    TipoMantenimiento defaultTipo = new TipoMantenimiento();
                    defaultTipo.setIdTipo(2); // Informativo por defecto
                    defaultTipo.setDescripcion("Informativo");
                    return tipoMantenimientoRepository.save(defaultTipo);
                });
        mantenimiento.setTipo(tipo);

        // Configurar estatus inicial como "Pendiente" (id=1)
        EstatusMantenimientoEntity estatus = estatusMantenimientoRepository.findById(1)
                .orElseGet(() -> {
                    // Si no existe, crear uno por defecto
                    EstatusMantenimientoEntity defaultEstatus = new EstatusMantenimientoEntity();
                    defaultEstatus.setIdEstatus(1);
                    defaultEstatus.setDescripcion("Pendiente");
                    return estatusMantenimientoRepository.save(defaultEstatus);
                });
        mantenimiento.setEstatus(estatus);

        return mantenimientoRepository.save(mantenimiento);
    }

    public List<MantenimientoDTO> obtenerHistorialReportes() {
        List<MantenimientoEntity> mantenimientos = mantenimientoRepository.findAllOrderByFechaAltaDesc();

        return mantenimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MantenimientoDTO> obtenerReportesPorCondominio(Integer condominioId) {
        List<MantenimientoEntity> mantenimientos = mantenimientoRepository.findByCondominioId(condominioId);

        return mantenimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MantenimientoDTO> obtenerReportesPorUsuario(Integer usuarioId) {
        List<MantenimientoEntity> mantenimientos = mantenimientoRepository.findByUsuarioId(usuarioId);

        // Si no hay resultados por usuarioId, buscar por nombre de usuario
        if (mantenimientos.isEmpty()) {
            mantenimientos = mantenimientoRepository.findAll().stream()
                    .filter(m -> {
                        String usuarioApartamento = m.getUsuarioApartamento();
                        return usuarioApartamento != null && usuarioApartamento.contains("ID:" + usuarioId);
                    })
                    .collect(Collectors.toList());
        }

        return mantenimientos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MantenimientoEntity actualizarEstatusAResuelto(Integer id) {
        MantenimientoEntity mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte de Mantenimiento no encontrado"));

        // Buscar estatus "Resuelto" (id=2)
        EstatusMantenimientoEntity estatusResuelto = estatusMantenimientoRepository.findById(2)
                .orElseGet(() -> {
                    // Si no existe, crear uno
                    EstatusMantenimientoEntity nuevo = new EstatusMantenimientoEntity();
                    nuevo.setIdEstatus(2);
                    nuevo.setDescripcion("Resuelto");
                    return estatusMantenimientoRepository.save(nuevo);
                });

        mantenimiento.setEstatus(estatusResuelto);
        mantenimiento.setFechaMod(new Date());

        return mantenimientoRepository.save(mantenimiento);
    }

    public MantenimientoEntity reabrirReporte(Integer id) {
        MantenimientoEntity mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte de Mantenimiento no encontrado"));

        // Buscar estatus "Pendiente" (id=1)
        EstatusMantenimientoEntity estatusPendiente = estatusMantenimientoRepository.findById(1)
                .orElseGet(() -> {
                    EstatusMantenimientoEntity nuevo = new EstatusMantenimientoEntity();
                    nuevo.setIdEstatus(1);
                    nuevo.setDescripcion("Pendiente");
                    return estatusMantenimientoRepository.save(nuevo);
                });

        mantenimiento.setEstatus(estatusPendiente);
        mantenimiento.setFechaMod(new Date());

        return mantenimientoRepository.save(mantenimiento);
    }

    public MantenimientoEntity cancelarReporte(Integer id) {
        MantenimientoEntity mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte de Mantenimiento no encontrado"));

        // Buscar estatus "Cancelado" (id=3)
        Optional<EstatusMantenimientoEntity> estatusCanceladoOpt = estatusMantenimientoRepository.findById(3);

        EstatusMantenimientoEntity estatusCancelado;
        if (estatusCanceladoOpt.isPresent()) {
            estatusCancelado = estatusCanceladoOpt.get();
        } else {
            // Si no existe estatus Cancelado, crear uno
            EstatusMantenimientoEntity nuevo = new EstatusMantenimientoEntity();
            nuevo.setIdEstatus(3);
            nuevo.setDescripcion("Cancelado");
            estatusCancelado = estatusMantenimientoRepository.save(nuevo);
        }

        mantenimiento.setEstatus(estatusCancelado);
        mantenimiento.setFechaMod(new Date());

        return mantenimientoRepository.save(mantenimiento);
    }

    public void eliminarReporte(Integer id) {
        if (!mantenimientoRepository.existsById(id)) {
            throw new RuntimeException("Reporte de Mantenimiento no encontrado");
        }
        mantenimientoRepository.deleteById(id);
    }

    private MantenimientoDTO convertToDTO(MantenimientoEntity mantenimiento) {
        return new MantenimientoDTO(
            mantenimiento.getIdMantenimiento(),
            mantenimiento.getFechaAlta(),
            mantenimiento.getTituloReporte(),
            mantenimiento.getMensaje(),
            mantenimiento.getTipo() != null ? mantenimiento.getTipo().getDescripcion() : "Informativo",
            mantenimiento.getEstatus() != null ? mantenimiento.getEstatus().getDescripcion() : "Pendiente",
            mantenimiento.getUsuarioApartamento(),
            mantenimiento.getCondominioId(),
            mantenimiento.getUsuarioId(),
            mantenimiento.getUbicacion(),
            mantenimiento.getNumeroCasa()
        );
    }
}