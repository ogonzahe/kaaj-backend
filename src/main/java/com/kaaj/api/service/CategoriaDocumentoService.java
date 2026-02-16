package com.kaaj.api.service;

import com.kaaj.api.dto.CategoriaDocumentoDTO;
import com.kaaj.api.model.CategoriaDocumento;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.repository.CategoriaDocumentoRepository;
import com.kaaj.api.repository.CondominioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoriaDocumentoService {

    private final CategoriaDocumentoRepository categoriaRepository;
    private final CondominioRepository condominioRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerTodasPorCondominio(Long condominioId) {
        List<CategoriaDocumento> categorias;

        if (condominioId != null) {
            categorias = categoriaRepository.findByCondominioIdOrderByNombreAsc(condominioId);
        } else {
            categorias = categoriaRepository.findAllByOrderByNombreAsc();
        }

        List<CategoriaDocumentoDTO> dtos = categorias.stream().map(cat -> {
            CategoriaDocumentoDTO dto = new CategoriaDocumentoDTO();
            dto.setId(cat.getId());
            dto.setNombre(cat.getNombre());
            dto.setDescripcion(cat.getDescripcion());
            dto.setColor(cat.getColor());
            if (cat.getCondominio() != null) {
                dto.setCondominioId(cat.getCondominio().getId().longValue());
            }
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("categorias", dtos);
        return response;
    }

    @Transactional
    public Map<String, Object> crearCategoria(CategoriaDocumentoDTO dto) {
        if (dto.getCondominioId() == null) {
            throw new RuntimeException("El condominio es requerido para crear una categoría");
        }

        // Verificar si ya existe una categoría con el mismo nombre en este condominio
        if (categoriaRepository.existsByCondominioIdAndNombreIgnoreCase(
                dto.getCondominioId(), dto.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con este nombre en el condominio");
        }

        Condominio condominio = condominioRepository.findById(dto.getCondominioId().intValue())
                .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));

        CategoriaDocumento categoria = new CategoriaDocumento();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setColor(dto.getColor() != null ? dto.getColor() : "#3b82f6");
        categoria.setCondominio(condominio);

        CategoriaDocumento guardada = categoriaRepository.save(categoria);

        CategoriaDocumentoDTO resultadoDto = new CategoriaDocumentoDTO();
        resultadoDto.setId(guardada.getId());
        resultadoDto.setNombre(guardada.getNombre());
        resultadoDto.setDescripcion(guardada.getDescripcion());
        resultadoDto.setColor(guardada.getColor());
        resultadoDto.setCondominioId(guardada.getCondominio().getId().longValue());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Categoría creada con éxito");
        response.put("categoria", resultadoDto);
        return response;
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    @Transactional
    public Map<String, Object> actualizarCategoria(Long id, CategoriaDocumentoDTO dto) {
        CategoriaDocumento categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Verificar si el nombre ya existe en el mismo condominio (excepto para esta categoría)
        if (dto.getNombre() != null && !dto.getNombre().equals(categoria.getNombre())) {
            if (categoriaRepository.existsByCondominioIdAndNombreIgnoreCase(
                    categoria.getCondominio().getId().longValue(), dto.getNombre())) {
                throw new RuntimeException("Ya existe una categoría con este nombre en el condominio");
            }
            categoria.setNombre(dto.getNombre());
        }

        if (dto.getDescripcion() != null) {
            categoria.setDescripcion(dto.getDescripcion());
        }

        if (dto.getColor() != null) {
            categoria.setColor(dto.getColor());
        }

        CategoriaDocumento actualizada = categoriaRepository.save(categoria);

        CategoriaDocumentoDTO resultadoDto = new CategoriaDocumentoDTO();
        resultadoDto.setId(actualizada.getId());
        resultadoDto.setNombre(actualizada.getNombre());
        resultadoDto.setDescripcion(actualizada.getDescripcion());
        resultadoDto.setColor(actualizada.getColor());
        resultadoDto.setCondominioId(actualizada.getCondominio().getId().longValue());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Categoría actualizada con éxito");
        response.put("categoria", resultadoDto);
        return response;
    }
}