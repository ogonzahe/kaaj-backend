package com.kaaj.api.controller;

import com.kaaj.api.dto.CategoriaDocumentoDTO;
import com.kaaj.api.service.CategoriaDocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/categorias-documentos") // <--- ESTO ARREGLA TU ERROR 404
@CrossOrigin(origins = "*") // Permite conexión desde React
public class CategoriaDocumentoController {

    @Autowired
    private CategoriaDocumentoService categoriaService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCategoria(@RequestBody CategoriaDocumentoDTO dto) {
        try {
            return ResponseEntity.ok(categoriaService.crearCategoria(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.ok(Map.of("mensaje", "Categoría eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}