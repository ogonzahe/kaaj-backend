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
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    // NUEVO MÉTODO: Obtener categorías por condominio
    @Transactional(readOnly = true)
    public List<CategoriaDocumentoDTO> obtenerCategoriasPorCondominio(Long condominioId) {
        List<CategoriaDocumento> categorias;

        if (condominioId != null) {
            categorias = categoriaDocumentoRepository.findByCondominioIdOrderByNombreAsc(condominioId);
        } else {
            // Si no se especifica condominio, devolver todas (para compatibilidad)
            categorias = categoriaDocumentoRepository.findAllByOrderByNombreAsc();
        }

        return categorias.stream()
                .map(this::convertirCategoriaADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> obtenerDocumentosParaUsuario(Long condominioUsuarioId, Long condominioFiltro, Long categoriaId) {
        List<Documento> documentos;

        Long condominioIdFinal = condominioFiltro != null ? condominioFiltro : condominioUsuarioId;

        if (condominioIdFinal != null) {
            if (categoriaId != null) {
                documentos = documentoRepository.findByCondominioIdAndCategoriaId(
                    condominioIdFinal, categoriaId);
            } else {
                documentos = documentoRepository.findByCondominioIdAndEsPublicoTrueOrCondominioId(
                    condominioIdFinal, condominioIdFinal);
            }
        } else {
            documentos = documentoRepository.findByEsPublicoTrue();
        }

        return documentos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> obtenerTodosDocumentos(Long condominioId, Long categoriaId) {
        List<Documento> documentos;

        if (condominioId != null && categoriaId != null) {
            documentos = documentoRepository.findByCondominioIdAndCategoriaId(condominioId, categoriaId);
        } else if (condominioId != null) {
            documentos = documentoRepository.findByCondominioId(condominioId);
        } else if (categoriaId != null) {
            documentos = documentoRepository.findByCategoriaId(categoriaId);
        } else {
            documentos = documentoRepository.findAll();
        }

        return documentos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> obtenerDocumentosPublicos(Long condominioId, Long categoriaId) {
        List<Documento> documentos;

        if (condominioId != null) {
            if (categoriaId != null) {
                documentos = documentoRepository.findByCondominioIdAndCategoriaIdAndEsPublicoTrue(
                    condominioId, categoriaId);
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

    @Transactional
    public DocumentoDTO crearDocumento(DocumentoDTO documentoDTO, MultipartFile archivo, Long usuarioId)
            throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("El archivo es requerido");
        }

        if (archivo.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo es demasiado grande. Máximo 10MB");
        }

        Documento documento = new Documento();
        documento.setTitulo(documentoDTO.getTitulo());
        documento.setDescripcion(documentoDTO.getDescripcion());

        if (documentoDTO.getCondominioId() != null) {
            Condominio condominio = condominioRepository.findById(documentoDTO.getCondominioId().intValue())
                    .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));
            documento.setCondominio(condominio);
        }

        if (documentoDTO.getCategoriaId() != null) {
            CategoriaDocumento categoria = categoriaDocumentoRepository.findById(documentoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            documento.setCategoria(categoria);
        }

        documento.setFechaVigencia(documentoDTO.getFechaVigencia());
        documento.setEsPublico(documentoDTO.getEsPublico() != null ? documentoDTO.getEsPublico() : true);

        Usuario usuario = usuarioRepository.findById(usuarioId.intValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        documento.setCreadoPor(usuario);

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

        Documento saved = documentoRepository.save(documento);
        return convertirADTO(saved);
    }

    @Transactional
    public DocumentoDTO actualizarDocumento(Long documentoId, DocumentoDTO documentoDTO, MultipartFile archivo,
            Long usuarioId) throws IOException {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        documento.setTitulo(documentoDTO.getTitulo());
        documento.setDescripcion(documentoDTO.getDescripcion());

        if (documentoDTO.getCondominioId() != null) {
            Condominio condominio = condominioRepository.findById(documentoDTO.getCondominioId().intValue())
                    .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));
            documento.setCondominio(condominio);
        } else {
            documento.setCondominio(null);
        }

        if (documentoDTO.getCategoriaId() != null) {
            CategoriaDocumento categoria = categoriaDocumentoRepository.findById(documentoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            documento.setCategoria(categoria);
        } else {
            documento.setCategoria(null);
        }

        documento.setEsPublico(documentoDTO.getEsPublico() != null ? documentoDTO.getEsPublico() : true);
        documento.setFechaVigencia(documentoDTO.getFechaVigencia());

        if (archivo != null && !archivo.isEmpty()) {
            if (archivo.getSize() > 10 * 1024 * 1024) {
                throw new RuntimeException("El archivo es demasiado grande. Máximo 10MB");
            }

            if (documento.getRutaArchivo() != null) {
                try {
                    Files.deleteIfExists(Paths.get(documento.getRutaArchivo()));
                } catch (IOException e) {
                    System.err.println("Error al eliminar archivo anterior: " + e.getMessage());
                }
            }

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

    @Transactional(readOnly = true)
    public List<CategoriaDocumentoDTO> obtenerCategorias() {
        return categoriaDocumentoRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertirCategoriaADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaDocumentoDTO crearCategoria(CategoriaDocumentoDTO categoriaDTO) {
        if (categoriaDTO.getCondominioId() == null) {
            throw new RuntimeException("El condominio es requerido para crear una categoría");
        }

        // Verificar si ya existe una categoría con el mismo nombre en este condominio
        if (categoriaDocumentoRepository.existsByCondominioIdAndNombreIgnoreCase(
                categoriaDTO.getCondominioId(), categoriaDTO.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre en este condominio");
        }

        Condominio condominio = condominioRepository.findById(categoriaDTO.getCondominioId().intValue())
                .orElseThrow(() -> new RuntimeException("Condominio no encontrado"));

        CategoriaDocumento categoria = new CategoriaDocumento();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setColor(categoriaDTO.getColor() != null ? categoriaDTO.getColor() : "#3b82f6");
        categoria.setCondominio(condominio);

        CategoriaDocumento saved = categoriaDocumentoRepository.save(categoria);
        return convertirCategoriaADTO(saved);
    }

    @Transactional
    public CategoriaDocumentoDTO actualizarCategoria(Long categoriaId, CategoriaDocumentoDTO categoriaDTO) {
        CategoriaDocumento categoria = categoriaDocumentoRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Verificar si el nombre ya existe en el mismo condominio (excepto para esta categoría)
        if (categoriaDTO.getNombre() != null && !categoriaDTO.getNombre().equals(categoria.getNombre())) {
            if (categoriaDocumentoRepository.existsByCondominioIdAndNombreIgnoreCase(
                    categoria.getCondominio().getId().longValue(), categoriaDTO.getNombre())) {
                throw new RuntimeException("Ya existe una categoría con ese nombre en este condominio");
            }
            categoria.setNombre(categoriaDTO.getNombre());
        }

        if (categoriaDTO.getDescripcion() != null) {
            categoria.setDescripcion(categoriaDTO.getDescripcion());
        }

        if (categoriaDTO.getColor() != null) {
            categoria.setColor(categoriaDTO.getColor());
        }

        CategoriaDocumento updated = categoriaDocumentoRepository.save(categoria);
        return convertirCategoriaADTO(updated);
    }

    @Transactional
    public void eliminarCategoria(Long categoriaId) {
        CategoriaDocumento categoria = categoriaDocumentoRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        List<Documento> documentosEnCategoria = documentoRepository.findByCategoriaId(categoriaId);
        if (!documentosEnCategoria.isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene documentos asociados");
        }

        categoriaDocumentoRepository.delete(categoria);
    }

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

    @Transactional
    public void eliminarDocumento(Long documentoId, Long usuarioId) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        try {
            if (documento.getRutaArchivo() != null) {
                Path path = Paths.get(documento.getRutaArchivo());
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Advertencia: No se pudo borrar el archivo físico: " + e.getMessage());
        }

        documentoRepository.delete(documento);
    }

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

    private CategoriaDocumentoDTO convertirCategoriaADTO(CategoriaDocumento categoria) {
        CategoriaDocumentoDTO dto = new CategoriaDocumentoDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setColor(categoria.getColor());
        if (categoria.getCondominio() != null) {
            dto.setCondominioId(categoria.getCondominio().getId().longValue());
        }
        return dto;
    }
}