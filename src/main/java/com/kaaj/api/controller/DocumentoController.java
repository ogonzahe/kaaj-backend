package com.kaaj.api.controller;

import com.kaaj.api.dto.DocumentoDTO;
import com.kaaj.api.dto.CategoriaDocumentoDTO;
import com.kaaj.api.service.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    // ========== ENDPOINTS PARA DOCUMENTOS ==========

    @PostMapping(value = "/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt, // Alias para compatibilidad
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt, // Alias para compatibilidad
            @RequestParam(value = "fechaVigencia", required = false) String fechaVigencia,
            @RequestParam(value = "esPublico", defaultValue = "true") Boolean esPublico,
            @RequestParam(value = "es_publico", defaultValue = "true") Boolean esPublicoAlt, // Alias para compatibilidad
            @RequestHeader(value = "X-Usuario-Id", defaultValue = "1") Long usuarioId) {

        try {
            System.out.println("=== CREANDO DOCUMENTO ===");
            System.out.println("titulo: " + titulo);
            System.out.println("condominioId (1): " + condominioId);
            System.out.println("condominio_id (2): " + condominioIdAlt);
            System.out.println("categoriaId (1): " + categoriaId);
            System.out.println("categoria_id (2): " + categoriaIdAlt);
            System.out.println("esPublico (1): " + esPublico);
            System.out.println("es_publico (2): " + esPublicoAlt);
            System.out.println("usuarioId: " + usuarioId);

            // Consolidar parámetros (usar el primero que tenga valor)
            Long condominioFinal = condominioId != null ? condominioId : condominioIdAlt;
            Long categoriaFinal = categoriaId != null ? categoriaId : categoriaIdAlt;
            Boolean esPublicoFinal = esPublicoAlt != null ? esPublicoAlt : esPublico;

            System.out.println("Valores consolidados:");
            System.out.println("condominioFinal: " + condominioFinal);
            System.out.println("categoriaFinal: " + categoriaFinal);
            System.out.println("esPublicoFinal: " + esPublicoFinal);

            DocumentoDTO documentoDTO = new DocumentoDTO();
            documentoDTO.setTitulo(titulo);
            documentoDTO.setDescripcion(descripcion);
            documentoDTO.setCondominioId(condominioFinal);
            documentoDTO.setCategoriaId(categoriaFinal);
            documentoDTO.setEsPublico(esPublicoFinal);

            if (fechaVigencia != null && !fechaVigencia.isEmpty()) {
                documentoDTO.setFechaVigencia(LocalDate.parse(fechaVigencia));
            }

            DocumentoDTO creado = documentoService.crearDocumento(documentoDTO, archivo, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento creado exitosamente");
            response.put("documento", creado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR al crear documento: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al crear documento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping(value = "/documentos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarDocumento(
            @PathVariable Long id,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt,
            @RequestParam(value = "fechaVigencia", required = false) String fechaVigencia,
            @RequestParam(value = "esPublico", defaultValue = "true") Boolean esPublico,
            @RequestParam(value = "es_publico", defaultValue = "true") Boolean esPublicoAlt,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = "1") Long usuarioId) {

        try {
            // Consolidar parámetros
            Long condominioFinal = condominioId != null ? condominioId : condominioIdAlt;
            Long categoriaFinal = categoriaId != null ? categoriaId : categoriaIdAlt;
            Boolean esPublicoFinal = esPublicoAlt != null ? esPublicoAlt : esPublico;

            DocumentoDTO documentoDTO = new DocumentoDTO();
            documentoDTO.setTitulo(titulo);
            documentoDTO.setDescripcion(descripcion);
            documentoDTO.setCondominioId(condominioFinal);
            documentoDTO.setCategoriaId(categoriaFinal);
            documentoDTO.setEsPublico(esPublicoFinal);

            if (fechaVigencia != null && !fechaVigencia.isEmpty()) {
                documentoDTO.setFechaVigencia(LocalDate.parse(fechaVigencia));
            }

            DocumentoDTO actualizado = documentoService.actualizarDocumento(id, documentoDTO, archivo, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento actualizado exitosamente");
            response.put("documento", actualizado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar documento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/documentos")
    public ResponseEntity<?> obtenerDocumentos(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt,
            @RequestParam(value = "publicos", defaultValue = "true") Boolean publicos) {

        try {
            System.out.println("=== OBTENIENDO DOCUMENTOS ===");
            System.out.println("condominioId (1): " + condominioId);
            System.out.println("condominio_id (2): " + condominioIdAlt);
            System.out.println("categoriaId (1): " + categoriaId);
            System.out.println("categoria_id (2): " + categoriaIdAlt);
            System.out.println("publicos: " + publicos);

            // Consolidar parámetros
            Long condominioFinal = condominioId != null ? condominioId : condominioIdAlt;
            Long categoriaFinal = categoriaId != null ? categoriaId : categoriaIdAlt;

            System.out.println("Valores consolidados:");
            System.out.println("condominioFinal: " + condominioFinal);
            System.out.println("categoriaFinal: " + categoriaFinal);

            List<DocumentoDTO> documentos;

            if (publicos) {
                documentos = documentoService.obtenerDocumentosPublicos(condominioFinal, categoriaFinal);
            } else {
                documentos = documentoService.obtenerTodosDocumentos(condominioFinal, categoriaFinal);
            }

            System.out.println("Documentos encontrados: " + documentos.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("documentos", documentos);
            response.put("total", documentos.size());
            response.put("condominioFiltrado", condominioFinal); // Para debug

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR al obtener documentos: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener documentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/documentos/{id}/download")
    public ResponseEntity<byte[]> descargarDocumento(@PathVariable Long id) {
        try {
            byte[] contenido = documentoService.descargarDocumento(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "documento");

            return new ResponseEntity<>(contenido, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/documentos/{id}")
    public ResponseEntity<?> eliminarDocumento(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = "1") Long usuarioId) {

        try {
            documentoService.eliminarDocumento(id, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento eliminado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar documento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========== ENDPOINTS PARA CATEGORÍAS ==========

    @GetMapping("/documentos/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        try {
            List<CategoriaDocumentoDTO> categorias = documentoService.obtenerCategorias();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categorias", categorias);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener categorías: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/documentos/categorias")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDocumentoDTO categoriaDTO) {
        try {
            CategoriaDocumentoDTO creada = documentoService.crearCategoria(categoriaDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría creada exitosamente");
            response.put("categoria", creada);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al crear categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/documentos/categorias/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            documentoService.eliminarCategoria(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría eliminada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}