package com.kaaj.api.service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kaaj.api.dto.MantenimientoDTO;
import com.kaaj.api.dto.ReporteMantenimientoDTO;
import com.kaaj.api.model.EstatusMantenimientoEntity;
import com.kaaj.api.model.MantenimientoEntity;
import com.kaaj.api.model.TipoMantenimiento;
import com.kaaj.api.repository.EstatusMantenimientoRepository;
import com.kaaj.api.repository.MantenimientoRepository;
import com.kaaj.api.repository.TipoMantenimientoRepository;

import jakarta.transaction.Transactional;

@Service
public class MantenimientoService {
    @Autowired
    private MantenimientoRepository mantenimientoRepository;
 
    @Autowired
    private TipoMantenimientoRepository tipoMantenimientoRepository;
    @Autowired
    private EstatusMantenimientoRepository estatusMantenimientoRepository;

    @Transactional
    public MantenimientoEntity crearMantenimiento(ReporteMantenimientoDTO dto) throws Exception {
        MantenimientoEntity nuevoMantenimiento = new MantenimientoEntity();
        final Integer ESTATUS_INICIAL_ID = 1; 
        nuevoMantenimiento.setTituloReporte(dto.getTituloReporte());
        nuevoMantenimiento.setUsuarioApartamento(dto.getUsuarioApartamento());
        nuevoMantenimiento.setMensaje(dto.getMensaje());

        TipoMantenimiento tipo = tipoMantenimientoRepository.findById(dto.getIdTipo())
            .orElseThrow(() -> new Exception("Tipo de Mantenimiento no válido."));
        nuevoMantenimiento.setTipo(tipo);

        EstatusMantenimientoEntity estatusInicial = estatusMantenimientoRepository.findById(ESTATUS_INICIAL_ID)
            .orElseThrow(() -> new Exception("Estatus inicial no encontrado."));
        nuevoMantenimiento.setEstatus(estatusInicial); 
        nuevoMantenimiento.setFechaAlta(new Date());

        return mantenimientoRepository.save(nuevoMantenimiento);
    }

    @Transactional 
    public MantenimientoEntity actualizarEstatusAResuelto(Integer idMantenimiento) throws Exception {

        final Integer ESTATUS_RESUELTO_ID = 2; 

        MantenimientoEntity mantenimiento = mantenimientoRepository.findById(idMantenimiento)
            .orElseThrow(() -> new Exception("Reporte de Mantenimiento no encontrado con ID: " + idMantenimiento));

        EstatusMantenimientoEntity estatusResuelto = estatusMantenimientoRepository.findById(ESTATUS_RESUELTO_ID)
            .orElseThrow(() -> new Exception("Estatus Resuelto no encontrado."));

        mantenimiento.setEstatus(estatusResuelto); 
        mantenimiento.setFechaMod(new Date());

        return mantenimientoRepository.save(mantenimiento);
    }

public List<MantenimientoDTO> obtenerHistorialReportes() {
    
    List<MantenimientoEntity> entidades = mantenimientoRepository.findAll();
    
    return entidades.stream()
        .map(entity -> {
            String tipoDesc = entity.getTipo().getDescripcion();
            String estatusDesc = entity.getEstatus().getDescripcion();
            
            return new MantenimientoDTO(
                entity.getIdMantenimiento(),
                entity.getFechaAlta(), 
                entity.getTituloReporte(),
                entity.getMensaje(),
                tipoDesc,
                estatusDesc
            );
        })
        .collect(Collectors.toList());
}

}
