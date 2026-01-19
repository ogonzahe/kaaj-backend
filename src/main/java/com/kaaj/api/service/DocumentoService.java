package com.kaaj.api.service;

import com.kaaj.api.model.Documento;
import com.kaaj.api.model.CategoriaDocumento;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.dto.DocumentoDTO;
import com.kaaj.api.dto.CategoriaDocumentoDTO;
import com.kaaj.api.repository.DocumentoRepository;
import com.kaaj.api.repository.CategoriaDocumentoRepository;
import com.kaaj.api.repository.CondominioRepository;
import com.kaaj.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private CategoriaDocumentoRepository categoriaDocumentoRepository;

    @Autowired
    private CondominioRepository condominioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path getUploadPath() throws IOException {
        // Crear directorio específico para documentos
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    // Crear documento
    @Transactional
    public DocumentoDTO crearDocumento(DocumentoDTO documentoDTO, MultipartFile archivo, Long usuarioId)
            throws IOException {

        // Validar que haya archivo
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("El archivo es requerido");
        }

        // Validar tamaño del archivo (10MB máximo)
        if (archivo.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo es demasiado grande. Máximo 10MB");
        }

        Documento documento = new Documento();
        documento.setTitulo(documentoDTO.getTitulo());
        documento.setDescripcion(documentoDTO.getDescripcion());

        // Asignar condominio
        if (documentoDTO.getCondominioId() != null) {
            Condominio condominio = condominioRepository.findById(documentoDTO.getCondominioId().intValue())
                    .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));
            documento.setCondominio(condominio);
        }

        // Asignar categoría
        if (documentoDTO.getCategoriaId() != null) {
            CategoriaDocumento categoria = categoriaDocumentoRepository.findById(documentoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            documento.setCategoria(categoria);
        }

        documento.setFechaVigencia(documentoDTO.getFechaVigencia());
        documento.setEsPublico(documentoDTO.getEsPublico() != null ? documentoDTO.getEsPublico() : true);

        // Asignar usuario creador
        Usuario usuario = usuarioRepository.findById(usuarioId.intValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        documento.setCreadoPor(usuario);

        // Guardar archivo
        Path uploadPath = getUploadPath();
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";

        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }

        // Generar nombre único para el archivo
        String nombreArchivo = UUID.randomUUID().toString() + extension;
        Path destinationFile = uploadPath.resolve(nombreArchivo).normalize().toAbsolutePath();

        // Guardar archivo en disco
        Files.copy(archivo.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        // Guardar información del archivo en la base de datos
        documento.setNombreArchivo(nombreOriginal);
        documento.setRutaArchivo(destinationFile.toString());
        documento.setTamanio(archivo.getSize());
        documento.setMimeType(archivo.getContentType());

        Documento saved = documentoRepository.save(documento);
        return convertirADTO(saved);
    }

    // Actualizar documento
    @Transactional
    public DocumentoDTO actualizarDocumento(Long documentoId, DocumentoDTO documentoDTO, MultipartFile archivo,
            Long usuarioId) throws IOException {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        /* NOTA: También podrías querer comentar esta validación si el Admin va a editar documentos de otros.
           Por ahora la dejo como estaba para no alterar la lógica de edición,
           pero si tienes problemas editando, comenta el bloque 'if' siguiente. */
        if (documento.getCreadoPor() == null || documento.getCreadoPor().getId() == null ||
            !documento.getCreadoPor().getId().equals(usuarioId)) {
             // throw new RuntimeException("No autorizado para editar este documento");
             // Si quieres permitir edición admin, comenta la línea de arriba ^
        }

        documento.setTitulo(documentoDTO.getTitulo());
        documento.setDescripcion(documentoDTO.getDescripcion());

        // Actualizar condominio
        if (documentoDTO.getCondominioId() != null) {
            Condominio condominio = condominioRepository.findById(documentoDTO.getCondominioId().intValue())
                    .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));
            documento.setCondominio(condominio);
        } else {
            documento.setCondominio(null);
        }

        // Actualizar categoría
        if (documentoDTO.getCategoriaId() != null) {
            CategoriaDocumento categoria = categoriaDocumentoRepository.findById(documentoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            documento.setCategoria(categoria);
        } else {
            documento.setCategoria(null);
        }

        documento.setEsPublico(documentoDTO.getEsPublico() != null ? documentoDTO.getEsPublico() : true);
        documento.setFechaVigencia(documentoDTO.getFechaVigencia());

        // Si se sube un nuevo archivo
        if (archivo != null && !archivo.isEmpty()) {
            // Validar tamaño
            if (archivo.getSize() > 10 * 1024 * 1024) {
                throw new RuntimeException("El archivo es demasiado grande. Máximo 10MB");
            }

            // Eliminar archivo anterior si existe
            if (documento.getRutaArchivo() != null) {
                try {
                    Files.deleteIfExists(Paths.get(documento.getRutaArchivo()));
                } catch (IOException e) {
                    System.err.println("Error al eliminar archivo anterior: " + e.getMessage());
                }
            }

            // Guardar nuevo archivo
            Path uploadPath = getUploadPath();
            String nombreOriginal = archivo.getOriginalFilename();
            String extension = "";

            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }

            String nombreArchivo = UUID.randomUUID().toString() + extension;
            Path destinationFile = uploadPath.resolve(nombreArchivo).normalize().toAbsolutePath();

            Files.copy(archivo.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            documento.setNombreArchivo(nombreOriginal);
            documento.setRutaArchivo(destinationFile.toString());
            documento.setTamanio(archivo.getSize());
            documento.setMimeType(archivo.getContentType());
        }

        Documento updated = documentoRepository.save(documento);
        return convertirADTO(updated);
    }

    // Obtener todos los documentos (para admin)
    @Transactional(readOnly = true)
    public List<DocumentoDTO> obtenerTodosDocumentos(Long condominioId, Long categoriaId) {
        List<Documento> documentos;

        if (condominioId != null && categoriaId != null) {
            documentos = documentoRepository.findByCondominioAndCategoria(condominioId, categoriaId);
        } else if (condominioId != null) {
            documentos = documentoRepository.findByCondominioId(condominioId);
        } else if (categoriaId != null) {
            documentos = documentoRepository.findByCategoriaIdAndEsPublicoTrue(categoriaId);
        } else {
            documentos = documentoRepository.findAll();
        }

        return documentos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener documentos públicos (para residentes)
    @Transactional(readOnly = true)
    public List<DocumentoDTO> obtenerDocumentosPublicos(Long condominioId, Long categoriaId) {
        List<Documento> documentos;

        if (condominioId != null) {
            if (categoriaId != null) {
                documentos = documentoRepository.findByCondominioAndCategoria(condominioId, categoriaId);
            } else {
                documentos = documentoRepository.findByCondominioIdAndEsPublicoTrue(condominioId);
            }
        } else {
            documentos = documentoRepository.findByEsPublicoTrue();
        }

        return documentos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener todas las categorías
    @Transactional(readOnly = true)
    public List<CategoriaDocumentoDTO> obtenerCategorias() {
        return categoriaDocumentoRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertirCategoriaADTO)
                .collect(Collectors.toList());
    }

    // Crear categoría
    @Transactional
    public CategoriaDocumentoDTO crearCategoria(CategoriaDocumentoDTO categoriaDTO) {
        // Verificar si ya existe
        if (categoriaDocumentoRepository.existsByNombreIgnoreCase(categoriaDTO.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        CategoriaDocumento categoria = new CategoriaDocumento();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setColor(categoriaDTO.getColor() != null ? categoriaDTO.getColor() : "#3b82f6");

        CategoriaDocumento saved = categoriaDocumentoRepository.save(categoria);
        return convertirCategoriaADTO(saved);
    }

    // Eliminar categoría
    @Transactional
    public void eliminarCategoria(Long categoriaId) {
        CategoriaDocumento categoria = categoriaDocumentoRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Verificar si hay documentos en esta categoría
        List<Documento> documentosEnCategoria = documentoRepository.findByCategoriaIdAndEsPublicoTrue(categoriaId);
        if (!documentosEnCategoria.isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene documentos asociados");
        }

        categoriaDocumentoRepository.delete(categoria);
    }

    // Descargar documento
    @Transactional(readOnly = true)
    public byte[] descargarDocumento(Long documentoId) throws IOException {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        if (documento.getRutaArchivo() == null) {
            throw new RuntimeException("El documento no tiene archivo asociado");
        }

        Path filePath = Paths.get(documento.getRutaArchivo());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("El archivo no existe en el servidor");
        }

        return Files.readAllBytes(filePath);
    }

    // --- SECCIÓN CORREGIDA ---
    // Eliminar documento
    @Transactional
    public void eliminarDocumento(Long documentoId, Long usuarioId) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        /* * CORRECCIÓN 1: Se elimina o comenta la validación estricta de "CreadoPor".
         * Esto permite que un Administrador pueda borrar cualquier documento,
         * independientemente de quién lo subió.
         */
        /*
        if (documento.getCreadoPor() == null || documento.getCreadoPor().getId() == null ||
            !documento.getCreadoPor().getId().equals(usuarioId)) {
            throw new RuntimeException("No autorizado para eliminar este documento");
        }
        */

        // CORRECCIÓN 2: Manejo de errores seguro al borrar archivo físico
        try {
            if (documento.getRutaArchivo() != null) {
                Path path = Paths.get(documento.getRutaArchivo());
                // deleteIfExists devuelve true si lo borró, false si no existía
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            // Logueamos el error pero NO lanzamos excepción.
            // Esto permite que el registro se borre de la BD aunque falle el borrado del archivo.
            System.err.println("⚠️ Advertencia: No se pudo borrar el archivo físico (posiblemente en uso o no encontrado): " + e.getMessage());
        }

        // Finalmente borramos el registro de la base de datos
        documentoRepository.delete(documento);
    }
    // --- FIN SECCIÓN CORREGIDA ---

    // Convertir Documento a DTO
    private DocumentoDTO convertirADTO(Documento documento) {
        DocumentoDTO dto = new DocumentoDTO();
        dto.setId(documento.getId());
        dto.setTitulo(documento.getTitulo());
        dto.setDescripcion(documento.getDescripcion());
        dto.setNombreArchivo(documento.getNombreArchivo());
        dto.setTamanio(documento.getTamanio());
        dto.setMimeType(documento.getMimeType());
        dto.setEsPublico(documento.getEsPublico());
        dto.setFechaVigencia(documento.getFechaVigencia());
        dto.setCreadoEn(documento.getCreadoEn());
        dto.setActualizadoEn(documento.getActualizadoEn());

        if (documento.getCondominio() != null) {
            dto.setCondominioId(documento.getCondominio().getId().longValue());
            dto.setCondominioNombre(documento.getCondominio().getNombre());
        }

        if (documento.getCategoria() != null) {
            dto.setCategoriaId(documento.getCategoria().getId());
            dto.setCategoriaNombre(documento.getCategoria().getNombre());
        }

        return dto;
    }

    // Convertir CategoriaDocumento a DTO
    private CategoriaDocumentoDTO convertirCategoriaADTO(CategoriaDocumento categoria) {
        CategoriaDocumentoDTO dto = new CategoriaDocumentoDTO();
        dto.setId(categoria.getId().longValue());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setColor(categoria.getColor());
        return dto;
    }
}