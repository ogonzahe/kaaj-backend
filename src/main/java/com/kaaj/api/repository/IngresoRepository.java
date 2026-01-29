package com.kaaj.api.repository;

import com.kaaj.api.model.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByCondominioId(Long condominioId);

    @Query("SELECT i FROM Ingreso i WHERE i.condominio.id = :condominioId AND i.año = :año ORDER BY i.mes, i.fecha")
    List<Ingreso> findByCondominioIdAndAño(@Param("condominioId") Long condominioId, @Param("año") Integer año);

    @Query("SELECT i FROM Ingreso i WHERE i.condominio.id = :condominioId AND i.año = :año AND i.mes = :mes ORDER BY i.fecha")
    List<Ingreso> findByCondominioIdAndAñoAndMes(@Param("condominioId") Long condominioId,
            @Param("año") Integer año,
            @Param("mes") Integer mes);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.condominio.id = :condominioId AND i.año = :año AND i.mes = :mes")
    BigDecimal calcularTotalPorMes(@Param("condominioId") Long condominioId,
            @Param("año") Integer año,
            @Param("mes") Integer mes);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.condominio.id = :condominioId AND i.año = :año")
    BigDecimal calcularTotalPorAño(@Param("condominioId") Long condominioId, @Param("año") Integer año);

    @Query("SELECT i FROM Ingreso i WHERE i.condominio.id = :condominioId AND i.año = :año ORDER BY i.mes DESC")
    List<Ingreso> findUltimosMeses(@Param("condominioId") Long condominioId, @Param("año") Integer año,
            org.springframework.data.domain.Pageable pageable);
}