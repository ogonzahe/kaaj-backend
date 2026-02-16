package com.kaaj.api.controller;

import com.kaaj.api.dto.DocumentoDTO;
import com.kaaj.api.dto.CategoriaDocumentoDTO;
import com.kaaj.api.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping(value = "/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt,
            @RequestParam(value = "fechaVigencia", required = false) String fechaVigencia,
            @RequestParam(value = "esPublico", defaultValue = "true") Boolean esPublico,
            @RequestParam(value = "es_publico", defaultValue = "true") Boolean esPublicoAlt,
            @RequestHeader(value = "X-Usuario-Id") Long usuarioId) {

        try {
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

            DocumentoDTO creado = documentoService.crearDocumento(documentoDTO, archivo, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento creado exitosamente");
            response.put("documento", creado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al crear documento", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al crear documento");
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
            @RequestHeader(value = "X-Usuario-Id") Long usuarioId) {

        try {
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
            log.error("Error al actualizar documento {}", id, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar documento");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/documentos")
    public ResponseEntity<?> obtenerDocumentos(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Condominio-Id", required = false) Long condominioUsuarioId) {

        try {
            Long condominioFinal = condominioId != null ? condominioId : condominioIdAlt;
            Long categoriaFinal = categoriaId != null ? categoriaId : categoriaIdAlt;

            List<DocumentoDTO> documentos;

            if ("admin_usuario".equals(userRole) || "COPO".equals(userRole)) {
                documentos = documentoService.obtenerTodosDocumentos(condominioFinal, categoriaFinal);
            } else if ("USUARIO".equals(userRole) || "SEGURIDAD".equals(userRole)) {
                documentos = documentoService.obtenerDocumentosParaUsuario(condominioUsuarioId, condominioFinal, categoriaFinal);
            } else {
                documentos = documentoService.obtenerDocumentosPublicos(condominioFinal, categoriaFinal);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("documentos", documentos);
            response.put("total", documentos.size());
            response.put("condominioFiltrado", condominioFinal);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener documentos", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener documentos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/documentos/admin")
    public ResponseEntity<?> obtenerDocumentosAdmin(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt,
            @RequestParam(value = "publicos", defaultValue = "false") Boolean publicos) {

        try {
            Long condominioFinal = condominioId != null ? condominioId : condominioIdAlt;
            Long categoriaFinal = categoriaId != null ? categoriaId : categoriaIdAlt;

            List<DocumentoDTO> documentos;

            if (publicos) {
                documentos = documentoService.obtenerDocumentosPublicos(condominioFinal, categoriaFinal);
            } else {
                documentos = documentoService.obtenerTodosDocumentos(condominioFinal, categoriaFinal);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("documentos", documentos);
            response.put("total", documentos.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener documentos admin", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener documentos");
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
            log.error("Error al descargar documento {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/documentos/{id}")
    public ResponseEntity<?> eliminarDocumento(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario-Id") Long usuarioId) {

        try {
            documentoService.eliminarDocumento(id, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento eliminado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al eliminar documento {}", id, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar documento");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========== ENDPOINTS PARA CATEGORIAS ==========

    @GetMapping("/documentos/categorias")
    public ResponseEntity<?> obtenerCategorias(
            @RequestParam(value = "condominioId", required = false) Long condominioId) {
        try {
            List<CategoriaDocumentoDTO> categorias = documentoService.obtenerCategoriasPorCondominio(condominioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categorias", categorias);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener categorias", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener categorias");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/documentos/categorias")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDocumentoDTO categoriaDTO) {
        try {
            CategoriaDocumentoDTO creada = documentoService.crearCategoria(categoriaDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoria creada exitosamente");
            response.put("categoria", creada);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error al crear categoria", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al crear categoria");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/documentos/categorias/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaDocumentoDTO categoriaDTO) {
        try {
            CategoriaDocumentoDTO actualizada = documentoService.actualizarCategoria(id, categoriaDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoria actualizada exitosamente");
            response.put("categoria", actualizada);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al actualizar categoria {}", id, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar categoria");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/documentos/categorias/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            documentoService.eliminarCategoria(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoria eliminada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al eliminar categoria {}", id, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar categoria");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
