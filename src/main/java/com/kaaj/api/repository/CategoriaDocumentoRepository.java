package com.kaaj.api.repository;

import com.kaaj.api.model.CategoriaDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaDocumentoRepository extends JpaRepository<CategoriaDocumento, Long> {
    List<CategoriaDocumento> findByCondominioIdOrderByNombreAsc(Long condominioId);
    List<CategoriaDocumento> findByCondominioIdOrderByNombreAsc(Integer condominioId);
    List<CategoriaDocumento> findAllByOrderByNombreAsc();

    // Verificar si existe por nombre y condominio
    boolean existsByCondominioIdAndNombreIgnoreCase(Long condominioId, String nombre);
    boolean existsByCondominioIdAndNombreIgnoreCase(Integer condominioId, String nombre);
}