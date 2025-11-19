package com.kaaj.service;

import com.kaaj.api.dto.CrearAmenidadDTO;
import com.kaaj.model.Amenidad;
import com.kaaj.repository.AmenidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AmenidadService {

    @Autowired
    private AmenidadRepository amenidadRepository;

    public List<Amenidad> listarTodas() {
        return amenidadRepository.findAll();
    }

    public Amenidad crearAmenidad(CrearAmenidadDTO dto) {
        Amenidad nueva = new Amenidad();
        
        // Mapeamos campos
        nueva.setNombre(dto.getNombre());
        nueva.setDescripcion(dto.getDescripcion());
        

        nueva.setCapacidad(dto.getCapacidad());
        nueva.setActiva(dto.getActiva());
        nueva.setImagenUrl(dto.getImagenUrl());

        return amenidadRepository.save(nueva);
    }
}