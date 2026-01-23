package com.kaaj.api.repository;

import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Usuario;
import com.kaaj.api.model.Condominio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Long> {

    Optional<Saldo> findTopByUsuarioOrderByActualizadoEnDesc(Usuario usuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM Saldo s WHERE s.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Integer usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Saldo s WHERE s.condominio.id = :condominioId")
    void deleteByCondominioId(@Param("condominioId") Integer condominioId);

    @Query("SELECT COALESCE(SUM(s.monto), 0) FROM Saldo s WHERE s.pagado = true AND s.monto > 0 " +
           "AND (:condominioId IS NULL OR s.usuario.condominio.id = :condominioId) " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularIngresosCondominio(@Param("condominioId") Long condominioId,
                                          @Param("fechaInicio") LocalDate fechaInicio,
                                          @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COALESCE(SUM(s.monto), 0) FROM Saldo s WHERE s.pagado = true AND s.monto > 0 " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularIngresosTotales(@Param("fechaInicio") LocalDate fechaInicio,
                                       @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COALESCE(SUM(s.monto), 0) FROM Saldo s WHERE s.pagado = false AND s.monto > 0 " +
           "AND (:condominioId IS NULL OR s.usuario.condominio.id = :condominioId) " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularEgresosCondominio(@Param("condominioId") Long condominioId,
                                         @Param("fechaInicio") LocalDate fechaInicio,
                                         @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COALESCE(SUM(s.monto), 0) FROM Saldo s WHERE s.pagado = false AND s.monto > 0 " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularEgresosTotales(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT s.id, 'INGRESO' as tipo, s.concepto, s.monto, s.fechaLimite, u.nombre, c.nombre " +
           "FROM Saldo s LEFT JOIN s.usuario u LEFT JOIN u.condominio c " +
           "WHERE s.pagado = true AND s.monto > 0 " +
           "AND (:condominioId IS NULL OR c.id = :condominioId) " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY s.fechaLimite DESC")
    Page<Object[]> findIngresosByCondominio(@Param("condominioId") Long condominioId,
                                            @Param("fechaInicio") LocalDate fechaInicio,
                                            @Param("fechaFin") LocalDate fechaFin,
                                            Pageable pageable);

    @Query("SELECT s.id, 'EGRESO' as tipo, s.concepto, s.monto, s.fechaLimite, u.nombre, c.nombre " +
           "FROM Saldo s LEFT JOIN s.usuario u LEFT JOIN u.condominio c " +
           "WHERE s.pagado = false AND s.monto > 0 " +
           "AND (:condominioId IS NULL OR c.id = :condominioId) " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY s.fechaLimite DESC")
    Page<Object[]> findEgresosByCondominio(@Param("condominioId") Long condominioId,
                                           @Param("fechaInicio") LocalDate fechaInicio,
                                           @Param("fechaFin") LocalDate fechaFin,
                                           Pageable pageable);

    @Query("SELECT s.id, CASE WHEN s.pagado = true THEN 'INGRESO' ELSE 'EGRESO' END as tipo, " +
           "s.concepto, s.monto, s.fechaLimite, u.nombre, c.nombre " +
           "FROM Saldo s LEFT JOIN s.usuario u LEFT JOIN u.condominio c " +
           "WHERE (:condominioId IS NULL OR c.id = :condominioId) " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY s.fechaLimite DESC")
    Page<Object[]> findAllMovimientosByCondominio(@Param("condominioId") Long condominioId,
                                                  @Param("fechaInicio") LocalDate fechaInicio,
                                                  @Param("fechaFin") LocalDate fechaFin,
                                                  Pageable pageable);

    @Query("SELECT s.id, 'INGRESO' as tipo, s.concepto, s.monto, s.fechaLimite, u.nombre, c.nombre " +
           "FROM Saldo s LEFT JOIN s.usuario u LEFT JOIN u.condominio c " +
           "WHERE s.pagado = true AND s.monto > 0 " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY s.fechaLimite DESC")
    Page<Object[]> findIngresosTotales(@Param("fechaInicio") LocalDate fechaInicio,
                                       @Param("fechaFin") LocalDate fechaFin,
                                       Pageable pageable);

    @Query("SELECT s.id, 'EGRESO' as tipo, s.concepto, s.monto, s.fechaLimite, u.nombre, c.nombre " +
           "FROM Saldo s LEFT JOIN s.usuario u LEFT JOIN u.condominio c " +
           "WHERE s.pagado = false AND s.monto > 0 " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY s.fechaLimite DESC")
    Page<Object[]> findEgresosTotales(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin,
                                      Pageable pageable);

    @Query("SELECT s.id, CASE WHEN s.pagado = true THEN 'INGRESO' ELSE 'EGRESO' END as tipo, " +
           "s.concepto, s.monto, s.fechaLimite, u.nombre, c.nombre " +
           "FROM Saldo s LEFT JOIN s.usuario u LEFT JOIN u.condominio c " +
           "WHERE s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY s.fechaLimite DESC")
    Page<Object[]> findAllMovimientos(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin,
                                      Pageable pageable);

    @Query("SELECT s.concepto, SUM(s.monto), " +
           "ROUND((SUM(s.monto) / (SELECT COALESCE(SUM(s2.monto), 1) FROM Saldo s2 " +
           "WHERE s2.pagado = true AND s2.monto > 0 " +
           "AND (:condominioId IS NULL OR s2.usuario.condominio.id = :condominioId) " +
           "AND s2.fechaLimite BETWEEN :fechaInicio AND :fechaFin)) * 100, 2), " +
           "COUNT(s.id) " +
           "FROM Saldo s " +
           "WHERE s.pagado = true AND s.monto > 0 " +
           "AND (:condominioId IS NULL OR s.usuario.condominio.id = :condominioId) " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY s.concepto " +
           "ORDER BY SUM(s.monto) DESC")
    List<Object[]> findDetalleIngresosCondominio(@Param("condominioId") Long condominioId,
                                                 @Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT s.concepto, SUM(s.monto), " +
           "ROUND((SUM(s.monto) / (SELECT COALESCE(SUM(s2.monto), 1) FROM Saldo s2 " +
           "WHERE s2.pagado = true AND s2.monto > 0 " +
           "AND s2.fechaLimite BETWEEN :fechaInicio AND :fechaFin)) * 100, 2), " +
           "COUNT(s.id) " +
           "FROM Saldo s " +
           "WHERE s.pagado = true AND s.monto > 0 " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY s.concepto " +
           "ORDER BY SUM(s.monto) DESC")
    List<Object[]> findDetalleIngresosTotales(@Param("fechaInicio") LocalDate fechaInicio,
                                              @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT s.concepto, SUM(s.monto), " +
           "ROUND((SUM(s.monto) / (SELECT COALESCE(SUM(s2.monto), 1) FROM Saldo s2 " +
           "WHERE s2.pagado = false AND s2.monto > 0 " +
           "AND (:condominioId IS NULL OR s2.usuario.condominio.id = :condominioId) " +
           "AND s2.fechaLimite BETWEEN :fechaInicio AND :fechaFin)) * 100, 2), " +
           "COUNT(s.id) " +
           "FROM Saldo s " +
           "WHERE s.pagado = false AND s.monto > 0 " +
           "AND (:condominioId IS NULL OR s.usuario.condominio.id = :condominioId) " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY s.concepto " +
           "ORDER BY SUM(s.monto) DESC")
    List<Object[]> findDetalleEgresosCondominio(@Param("condominioId") Long condominioId,
                                                @Param("fechaInicio") LocalDate fechaInicio,
                                                @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT s.concepto, SUM(s.monto), " +
           "ROUND((SUM(s.monto) / (SELECT COALESCE(SUM(s2.monto), 1) FROM Saldo s2 " +
           "WHERE s2.pagado = false AND s2.monto > 0 " +
           "AND s2.fechaLimite BETWEEN :fechaInicio AND :fechaFin)) * 100, 2), " +
           "COUNT(s.id) " +
           "FROM Saldo s " +
           "WHERE s.pagado = false AND s.monto > 0 " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY s.concepto " +
           "ORDER BY SUM(s.monto) DESC")
    List<Object[]> findDetalleEgresosTotales(@Param("fechaInicio") LocalDate fechaInicio,
                                             @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT s FROM Saldo s WHERE s.pagado = false " +
           "AND (:condominioId IS NULL OR s.usuario.condominio.id = :condominioId) " +
           "ORDER BY s.fechaLimite ASC")
    List<Saldo> findSaldosPendientesCondominio(@Param("condominioId") Long condominioId);

    @Query("SELECT s FROM Saldo s WHERE s.pagado = false ORDER BY s.fechaLimite ASC")
    List<Saldo> findSaldosPendientes();

    @Query("SELECT s FROM Saldo s WHERE " +
           "(:condominioId IS NULL OR s.usuario.condominio.id = :condominioId) " +
           "AND (:usuarioId IS NULL OR s.usuario.id = :usuarioId)")
    List<Saldo> findByCondominioIdAndUsuarioId(@Param("condominioId") Long condominioId,
                                               @Param("usuarioId") Long usuarioId);

    @Query("SELECT s FROM Saldo s WHERE s.pagado = false AND s.usuario = :usuario ORDER BY s.fechaLimite ASC")
    List<Saldo> findSaldosPendientesByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT s FROM Saldo s WHERE s.usuario.id = :usuarioId")
    List<Saldo> findByUsuarioId(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT s FROM Saldo s WHERE s.pagoProgramadoId = :pagoProgramadoId")
    List<Saldo> findByPagoProgramadoId(@Param("pagoProgramadoId") Integer pagoProgramadoId);

    @Query("SELECT s FROM Saldo s WHERE s.usuario = :usuario AND s.pagoProgramadoId = :pagoProgramadoId AND s.numeroRepeticion = :numeroRepeticion")
    List<Saldo> findByUsuarioAndPagoProgramadoIdAndNumeroRepeticion(
            @Param("usuario") Usuario usuario,
            @Param("pagoProgramadoId") Integer pagoProgramadoId,
            @Param("numeroRepeticion") Integer numeroRepeticion);

    @Query("SELECT COUNT(s) FROM Saldo s WHERE s.pagoProgramadoId = :pagoProgramadoId AND s.pagado = false")
    Long countByPagoProgramadoIdAndPendientes(@Param("pagoProgramadoId") Integer pagoProgramadoId);

    @Query("SELECT s FROM Saldo s WHERE s.usuario.condominio.id = :condominioId")
    List<Saldo> findByCondominioId(@Param("condominioId") Integer condominioId);

    @Query("SELECT s FROM Saldo s WHERE s.usuario = :usuario AND s.condominio = :condominio AND s.pagado = false ORDER BY s.fechaLimite ASC")
    List<Saldo> findByUsuarioAndCondominioAndPagadoFalse(@Param("usuario") Usuario usuario,
                                                         @Param("condominio") Condominio condominio);

    @Query("SELECT s FROM Saldo s WHERE s.usuario.id = :usuarioId AND s.condominio.id = :condominioId AND s.pagado = false ORDER BY s.fechaLimite ASC")
    List<Saldo> findByUsuarioIdAndCondominioIdAndPagadoFalse(@Param("usuarioId") Integer usuarioId,
                                                             @Param("condominioId") Integer condominioId);

    // ========== MÉTODOS NUEVOS PARA CONTRALORÍA ==========

    /**
     * Encontrar saldos por condominio usando Long
     */
    @Query("SELECT s FROM Saldo s WHERE s.usuario.condominio.id = :condominioId")
    List<Saldo> findByUsuarioCondominioId(@Param("condominioId") Long condominioId);

    /**
     * Encontrar ingresos por condominio y fecha
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE u.condominio.id = :condominioId " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = true " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findIngresosByCondominioAndFecha(
            @Param("condominioId") Long condominioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Encontrar egresos por condominio y fecha
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE u.condominio.id = :condominioId " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = false " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findEgresosByCondominioAndFecha(
            @Param("condominioId") Long condominioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Encontrar ingresos por fecha
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = true " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findIngresosByFecha(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Encontrar egresos por fecha
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = false " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findEgresosByFecha(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Contar ingresos por condominio
     */
    @Query("SELECT COUNT(s) FROM Saldo s WHERE s.usuario.condominio.id = :condominioId " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = true")
    long countIngresosCondominio(
            @Param("condominioId") Long condominioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Contar egresos por condominio
     */
    @Query("SELECT COUNT(s) FROM Saldo s WHERE s.usuario.condominio.id = :condominioId " +
           "AND s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = false")
    long countEgresosCondominio(
            @Param("condominioId") Long condominioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Contar ingresos totales
     */
    @Query("SELECT COUNT(s) FROM Saldo s WHERE s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = true")
    long countIngresosTotales(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Contar egresos totales
     */
    @Query("SELECT COUNT(s) FROM Saldo s WHERE s.fechaLimite BETWEEN :fechaInicio AND :fechaFin " +
           "AND s.monto > 0 AND s.pagado = false")
    long countEgresosTotales(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Encontrar ingresos por condominio (sin fecha)
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE u.condominio.id = :condominioId AND s.monto > 0 AND s.pagado = true " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findIngresosByCondominio(@Param("condominioId") Long condominioId);

    /**
     * Encontrar egresos por condominio (sin fecha)
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE u.condominio.id = :condominioId AND s.monto > 0 AND s.pagado = false " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findEgresosByCondominio(@Param("condominioId") Long condominioId);

    /**
     * Encontrar ingresos totales (sin fecha)
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.monto > 0 AND s.pagado = true ORDER BY s.fechaLimite DESC")
    List<Object[]> findIngresosTotales();

    /**
     * Encontrar egresos totales (sin fecha)
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.monto > 0 AND s.pagado = false ORDER BY s.fechaLimite DESC")
    List<Object[]> findEgresosTotales();

    /**
     * Encontrar saldos por año y mes específicos
     */
    @Query("SELECT s FROM Saldo s WHERE YEAR(s.fechaLimite) = :año AND MONTH(s.fechaLimite) = :mes")
    List<Saldo> findByYearAndMonth(@Param("año") int año, @Param("mes") int mes);

    /**
     * Obtener resumen por año
     */
    @Query("SELECT YEAR(s.fechaLimite) as año, " +
           "SUM(CASE WHEN s.monto > 0 AND s.pagado = true THEN s.monto ELSE 0 END) as ingresos, " +
           "SUM(CASE WHEN s.monto > 0 AND s.pagado = false THEN s.monto ELSE 0 END) as egresos, " +
           "COUNT(CASE WHEN s.monto > 0 AND s.pagado = true THEN 1 END) as countIngresos, " +
           "COUNT(CASE WHEN s.monto > 0 AND s.pagado = false THEN 1 END) as countEgresos " +
           "FROM Saldo s " +
           "WHERE s.fechaLimite IS NOT NULL " +
           "GROUP BY YEAR(s.fechaLimite) " +
           "ORDER BY YEAR(s.fechaLimite) DESC")
    List<Object[]> findResumenPorAño();

    /**
     * Obtener totales por mes para un año específico
     */
    @Query("SELECT MONTH(s.fechaLimite) as mes, " +
           "SUM(CASE WHEN s.monto > 0 AND s.pagado = true THEN s.monto ELSE 0 END) as ingresos, " +
           "SUM(CASE WHEN s.monto > 0 AND s.pagado = false THEN s.monto ELSE 0 END) as egresos " +
           "FROM Saldo s " +
           "WHERE YEAR(s.fechaLimite) = :año AND s.fechaLimite IS NOT NULL " +
           "GROUP BY MONTH(s.fechaLimite) " +
           "ORDER BY MONTH(s.fechaLimite)")
    List<Object[]> findTotalesPorMes(@Param("año") int año);

    /**
     * Obtener los 5 mayores ingresos
     */
    @Query("SELECT s.concepto, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.monto > 0 AND s.pagado = true " +
           "ORDER BY s.monto DESC LIMIT 5")
    List<Object[]> findTop5Ingresos();

    /**
     * Obtener los 5 mayores egresos
     */
    @Query("SELECT s.concepto, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.monto > 0 AND s.pagado = false " +
           "ORDER BY s.monto DESC LIMIT 5")
    List<Object[]> findTop5Egresos();

    /**
     * Obtener estadísticas de pagos por estado
     */
    @Query("SELECT COUNT(s) as total, " +
           "SUM(CASE WHEN s.pagado = true THEN 1 ELSE 0 END) as pagados, " +
           "SUM(CASE WHEN s.pagado = false THEN 1 ELSE 0 END) as pendientes, " +
           "AVG(s.monto) as promedio " +
           "FROM Saldo s " +
           "WHERE s.monto > 0")
    Object[] findEstadisticasGenerales();

    /**
     * Obtener saldos vencidos
     */
    @Query("SELECT s FROM Saldo s WHERE s.pagado = false AND s.fechaLimite < CURRENT_DATE")
    List<Saldo> findSaldosVencidos();

    /**
     * Obtener saldos vencidos por condominio
     */
    @Query("SELECT s FROM Saldo s WHERE s.pagado = false AND s.fechaLimite < CURRENT_DATE " +
           "AND s.usuario.condominio.id = :condominioId")
    List<Saldo> findSaldosVencidosByCondominio(@Param("condominioId") Long condominioId);

    /**
     * Obtener saldos por condominio
     */
    @Query("SELECT s FROM Saldo s WHERE s.usuario.condominio.id = :condominioId")
    List<Saldo> findByUsuarioCondominioId(@Param("condominioId") Integer condominioId);

    /**
     * Obtener ingresos detallados por usuario
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.usuario.id = :usuarioId AND s.monto > 0 AND s.pagado = true " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findIngresosByUsuarioId(@Param("usuarioId") Integer usuarioId);

    /**
     * Obtener egresos detallados por usuario
     */
    @Query("SELECT s.id, s.concepto, s.descripcion, s.monto, s.fechaLimite, u.nombre " +
           "FROM Saldo s JOIN s.usuario u " +
           "WHERE s.usuario.id = :usuarioId AND s.monto > 0 AND s.pagado = false " +
           "ORDER BY s.fechaLimite DESC")
    List<Object[]> findEgresosByUsuarioId(@Param("usuarioId") Integer usuarioId);
}