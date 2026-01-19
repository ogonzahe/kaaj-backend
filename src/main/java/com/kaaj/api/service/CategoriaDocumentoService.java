package com.kaaj.api.service;

import com.kaaj.api.dto.CategoriaDocumentoDTO;
import com.kaaj.api.model.CategoriaDocumento;
import com.kaaj.api.repository.CategoriaDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoriaDocumentoService {

    @Autowired
    private CategoriaDocumentoRepository categoriaRepository;

    public Map<String, Object> obtenerTodas() {
        List<CategoriaDocumento> categorias = categoriaRepository.findAllByOrderByNombreAsc();

        List<CategoriaDocumentoDTO> dtos = categorias.stream().map(cat -> {
            CategoriaDocumentoDTO dto = new CategoriaDocumentoDTO();
            dto.setId(cat.getId()); // Ya es Long, no necesita conversión
            dto.setNombre(cat.getNombre());
            dto.setDescripcion(cat.getDescripcion());
            dto.setColor(cat.getColor());
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("categorias", dtos);
        return response;
    }

    public Map<String, Object> crearCategoria(CategoriaDocumentoDTO dto) {
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con este nombre");
        }

        CategoriaDocumento categoria = new CategoriaDocumento();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setColor(dto.getColor());

        CategoriaDocumento guardada = categoriaRepository.save(categoria);

        CategoriaDocumentoDTO resultadoDto = new CategoriaDocumentoDTO();
        resultadoDto.setId(guardada.getId());
        resultadoDto.setNombre(guardada.getNombre());
        resultadoDto.setDescripcion(guardada.getDescripcion());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Categoría creada con éxito");
        response.put("categoria", resultadoDto);
        return response;
    }

    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}