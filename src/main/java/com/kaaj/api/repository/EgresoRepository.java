package com.kaaj.api.repository;

import com.kaaj.api.model.Egreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    List<Egreso> findByCondominioId(Long condominioId);

    // CAMBIO: usar "e.anio" en lugar de "e.año"
    @Query("SELECT e FROM Egreso e WHERE e.condominio.id = :condominioId AND e.anio = :año ORDER BY e.mes, e.fecha")
    List<Egreso> findByCondominioIdAndAño(@Param("condominioId") Long condominioId, @Param("año") Integer año);

    // CAMBIO: usar "e.anio" en lugar de "e.año"
    @Query("SELECT e FROM Egreso e WHERE e.condominio.id = :condominioId AND e.anio = :año AND e.mes = :mes ORDER BY e.fecha")
    List<Egreso> findByCondominioIdAndAñoAndMes(@Param("condominioId") Long condominioId,
            @Param("año") Integer año,
            @Param("mes") Integer mes);

    // CAMBIO: usar "e.anio" en lugar de "e.año"
    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.condominio.id = :condominioId AND e.anio = :año AND e.mes = :mes")
    BigDecimal calcularTotalPorMes(@Param("condominioId") Long condominioId,
            @Param("año") Integer año,
            @Param("mes") Integer mes);

    // CAMBIO: usar "e.anio" en lugar de "e.año"
    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e WHERE e.condominio.id = :condominioId AND e.anio = :año")
    BigDecimal calcularTotalPorAño(@Param("condominioId") Long condominioId, @Param("año") Integer año);

    // CAMBIO: usar "e.anio" en lugar de "e.año"
    @Query("SELECT e FROM Egreso e WHERE e.condominio.id = :condominioId AND e.anio = :año ORDER BY e.mes DESC")
    List<Egreso> findUltimosMeses(@Param("condominioId") Long condominioId, @Param("año") Integer año,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT e FROM Egreso e LEFT JOIN FETCH e.condominio LEFT JOIN FETCH e.usuario " +
           "WHERE e.condominio.id IN :condominioIds")
    List<Egreso> findByCondominioIdIn(@Param("condominioIds") List<Long> condominioIds);

    @Query("SELECT e FROM Egreso e LEFT JOIN FETCH e.condominio LEFT JOIN FETCH e.usuario " +
           "WHERE e.condominio.id IN :condominioIds AND e.anio = :año")
    List<Egreso> findByCondominioIdInAndAnio(@Param("condominioIds") List<Long> condominioIds,
                                             @Param("año") Integer año);

    @Query("SELECT e FROM Egreso e LEFT JOIN FETCH e.condominio LEFT JOIN FETCH e.usuario " +
           "WHERE e.anio = :año")
    List<Egreso> findByAnio(@Param("año") Integer año);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e " +
           "WHERE e.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularEgresosTotalesPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
                                              @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e " +
           "WHERE e.condominio.id = :condominioId " +
           "AND e.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularEgresosCondominioPorFecha(@Param("condominioId") Long condominioId,
                                                 @Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COALESCE(SUM(e.monto), 0) FROM Egreso e " +
           "WHERE e.condominio.id IN :condominioIds " +
           "AND e.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularEgresosCondominiosPorFecha(@Param("condominioIds") List<Long> condominioIds,
                                                  @Param("fechaInicio") LocalDate fechaInicio,
                                                  @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(e) FROM Egreso e WHERE e.fecha BETWEEN :fechaInicio AND :fechaFin")
    long countEgresosTotalesPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
                                     @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(e) FROM Egreso e WHERE e.condominio.id = :condominioId " +
           "AND e.fecha BETWEEN :fechaInicio AND :fechaFin")
    long countEgresosCondominioPorFecha(@Param("condominioId") Long condominioId,
                                        @Param("fechaInicio") LocalDate fechaInicio,
                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(e) FROM Egreso e WHERE e.condominio.id IN :condominioIds " +
           "AND e.fecha BETWEEN :fechaInicio AND :fechaFin")
    long countEgresosCondominiosPorFecha(@Param("condominioIds") List<Long> condominioIds,
                                         @Param("fechaInicio") LocalDate fechaInicio,
                                         @Param("fechaFin") LocalDate fechaFin);
}