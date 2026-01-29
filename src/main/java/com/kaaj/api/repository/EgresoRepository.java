package com.kaaj.api.repository;

import com.kaaj.api.model.Egreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    List<Egreso> findByCondominioId(Long condominioId);

    @Query("SELECT e FROM Egreso e WHERE e.condominio.id = :condominioId AND e.año = :año ORDER BY e.mes, e.fecha")
    List<Egreso> findByCondominioIdAndAño(@Param("condominioId") Long condominioId, @Param("año") Integer año);

    @Query("SELECT e FROM Egreso e WHERE e.condominio.id = :condominioId AND e.año = :año AND e.mes = :mes ORDER BY e.fecha")
    List<Egreso> findByCondominioIdAndAñoAndMes(@Param("condominioId") Long condominioId,
            @Param("año") Integer año,
            @Param("mes") Integer mes);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.condominio.id = :condominioId AND e.año = :año AND e.mes = :mes")
    BigDecimal calcularTotalPorMes(@Param("condominioId") Long condominioId,
            @Param("año") Integer año,
            @Param("mes") Integer mes);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.condominio.id = :condominioId AND e.año = :año")
    BigDecimal calcularTotalPorAño(@Param("condominioId") Long condominioId, @Param("año") Integer año);

    @Query("SELECT e FROM Egreso e WHERE e.condominio.id = :condominioId AND e.año = :año ORDER BY e.mes DESC")
    List<Egreso> findUltimosMeses(@Param("condominioId") Long condominioId, @Param("año") Integer año,
            org.springframework.data.domain.Pageable pageable);
}