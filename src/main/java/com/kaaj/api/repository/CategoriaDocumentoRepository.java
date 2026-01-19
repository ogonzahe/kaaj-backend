package com.kaaj.api.repository;

import com.kaaj.api.model.CategoriaDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaDocumentoRepository extends JpaRepository<CategoriaDocumento, Long> {
    List<CategoriaDocumento> findAllByOrderByNombreAsc();

    // Agregar este método para verificar si existe por nombre
    boolean existsByNombreIgnoreCase(String nombre);
}