package com.kaaj.api.repository;

import com.kaaj.api.model.Documento;
import com.kaaj.api.model.Condominio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByCondominioId(Long condominioId);

    List<Documento> findByCondominioIdAndEsPublicoTrue(Long condominioId);

    List<Documento> findByEsPublicoTrue();

    @Query("SELECT d FROM Documento d WHERE d.condominio.id = :condominioId AND " +
            "(:categoriaId IS NULL OR d.categoria.id = :categoriaId) AND " +
            "d.esPublico = true")
    List<Documento> findByCondominioAndCategoria(
            @Param("condominioId") Long condominioId,
            @Param("categoriaId") Long categoriaId);

    List<Documento> findByCategoriaIdAndEsPublicoTrue(Long categoriaId);
}