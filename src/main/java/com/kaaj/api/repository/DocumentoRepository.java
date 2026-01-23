package com.kaaj.api.repository;

import com.kaaj.api.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    // Métodos existentes
    List<Documento> findByCondominioId(Long condominioId);
    List<Documento> findByCondominioIdAndEsPublicoTrue(Long condominioId);
    List<Documento> findByEsPublicoTrue();
    List<Documento> findByCategoriaId(Long categoriaId);

    // Nuevos métodos necesarios
    @Query("SELECT d FROM Documento d WHERE d.condominio.id = :condominioId AND d.categoria.id = :categoriaId")
    List<Documento> findByCondominioIdAndCategoriaId(
            @Param("condominioId") Long condominioId,
            @Param("categoriaId") Long categoriaId);

    @Query("SELECT d FROM Documento d WHERE d.condominio.id = :condominioId AND " +
           "d.categoria.id = :categoriaId AND d.esPublico = true")
    List<Documento> findByCondominioIdAndCategoriaIdAndEsPublicoTrue(
            @Param("condominioId") Long condominioId,
            @Param("categoriaId") Long categoriaId);

    // Método CRÍTICO: documentos públicos + privados del mismo condominio
    @Query("SELECT d FROM Documento d WHERE " +
           "(d.condominio.id = :condominioId AND d.esPublico = true) OR " +
           "(d.condominio.id = :mismoCondominioId)")
    List<Documento> findByCondominioIdAndEsPublicoTrueOrCondominioId(
            @Param("condominioId") Long condominioId,
            @Param("mismoCondominioId") Long mismoCondominioId);
}