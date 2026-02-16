package com.kaaj.api.controller;

import com.kaaj.api.dto.CategoriaDocumentoDTO;
import com.kaaj.api.service.CategoriaDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CategoriaDocumentoController {

    private final CategoriaDocumentoService categoriaService;

    @GetMapping("/categorias-documentos")
    public ResponseEntity<?> obtenerCategorias(
            @RequestParam(value = "condominioId", required = false) Long condominioId) {
        try {
            Map<String, Object> response = categoriaService.obtenerTodasPorCondominio(condominioId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "message", "Error al obtener categorías: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/categorias-documentos")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDocumentoDTO categoriaDTO) {
        try {
            Map<String, Object> response = categoriaService.crearCategoria(categoriaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "message", "Error al crear categoría: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/categorias-documentos/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id,
                                                @RequestBody CategoriaDocumentoDTO categoriaDTO) {
        try {
            Map<String, Object> response = categoriaService.actualizarCategoria(id, categoriaDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "message", "Error al actualizar categoría: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/categorias-documentos/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Categoría eliminada exitosamente"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "message", "Error al eliminar categoría: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}