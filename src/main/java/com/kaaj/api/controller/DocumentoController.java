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
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt,
            @RequestParam(value = "fechaVigencia", required = false) String fechaVigencia,
            @RequestParam(value = "esPublico", defaultValue = "true") Boolean esPublico,
            @RequestParam(value = "es_publico", defaultValue = "true") Boolean esPublicoAlt,
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
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Condominio-Id", required = false) Long condominioUsuarioId) {

        try {
            System.out.println("=== OBTENIENDO DOCUMENTOS (PÚBLICO) ===");
            System.out.println("Usuario ID: " + usuarioId);
            System.out.println("User Role: " + userRole);
            System.out.println("Condominio Usuario: " + condominioUsuarioId);

            Long condominioFinal = condominioId != null ? condominioId : condominioIdAlt;
            Long categoriaFinal = categoriaId != null ? categoriaId : categoriaIdAlt;

            List<DocumentoDTO> documentos;

            // LÓGICA MEJORADA: Documentos visibles según rol
            if ("admin_usuario".equals(userRole) || "COPO".equals(userRole)) {
                // Admin/COPO ve TODOS los documentos (públicos y privados)
                System.out.println("🔑 Admin/COPO: Mostrando TODOS los documentos");
                documentos = documentoService.obtenerTodosDocumentos(condominioFinal, categoriaFinal);
            } else if ("USUARIO".equals(userRole) || "SEGURIDAD".equals(userRole)) {
                // Usuario/seguridad ve documentos públicos + privados de SU condominio
                System.out.println("👤 Usuario/Seguridad: Mostrando documentos del condominio " + condominioUsuarioId);
                documentos = documentoService.obtenerDocumentosParaUsuario(condominioUsuarioId, condominioFinal, categoriaFinal);
            } else {
                // Usuario no autenticado o rol desconocido: solo documentos públicos
                System.out.println("👤 Usuario no autenticado: Mostrando solo documentos públicos");
                documentos = documentoService.obtenerDocumentosPublicos(condominioFinal, categoriaFinal);
            }

            System.out.println("Documentos encontrados: " + documentos.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("documentos", documentos);
            response.put("total", documentos.size());
            response.put("condominioFiltrado", condominioFinal);

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

    @GetMapping("/documentos/admin")
    public ResponseEntity<?> obtenerDocumentosAdmin(
            @RequestParam(value = "condominioId", required = false) Long condominioId,
            @RequestParam(value = "condominio_id", required = false) Long condominioIdAlt,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoria_id", required = false) Long categoriaIdAlt,
            @RequestParam(value = "publicos", defaultValue = "false") Boolean publicos) {

        try {
            System.out.println("=== OBTENIENDO DOCUMENTOS (ADMIN) ===");
            System.out.println("publicos: " + publicos);

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
            System.err.println("ERROR al obtener documentos admin: " + e.getMessage());
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
    public ResponseEntity<?> obtenerCategorias(
            @RequestParam(value = "condominioId", required = false) Long condominioId) {
        try {
            System.out.println("=== OBTENIENDO CATEGORÍAS ===");
            System.out.println("condominioId: " + condominioId);

            List<CategoriaDocumentoDTO> categorias = documentoService.obtenerCategoriasPorCondominio(condominioId);

            System.out.println("Categorías encontradas: " + categorias.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categorias", categorias);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR al obtener categorías: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener categorías: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/documentos/categorias")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDocumentoDTO categoriaDTO) {
        try {
            System.out.println("=== CREANDO CATEGORÍA ===");
            System.out.println("Nombre: " + categoriaDTO.getNombre());
            System.out.println("CondominioId: " + categoriaDTO.getCondominioId());

            CategoriaDocumentoDTO creada = documentoService.crearCategoria(categoriaDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría creada exitosamente");
            response.put("categoria", creada);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("ERROR al crear categoría: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al crear categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/documentos/categorias/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaDocumentoDTO categoriaDTO) {
        try {
            System.out.println("=== ACTUALIZANDO CATEGORÍA ===");
            System.out.println("ID: " + id);
            System.out.println("Nombre: " + categoriaDTO.getNombre());

            CategoriaDocumentoDTO actualizada = documentoService.actualizarCategoria(id, categoriaDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría actualizada exitosamente");
            response.put("categoria", actualizada);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR al actualizar categoría: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/documentos/categorias/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            System.out.println("=== ELIMINANDO CATEGORÍA ===");
            System.out.println("ID: " + id);

            documentoService.eliminarCategoria(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría eliminada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR al eliminar categoría: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}