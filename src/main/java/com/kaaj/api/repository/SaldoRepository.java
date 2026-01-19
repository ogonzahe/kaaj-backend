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

    // Método existente
    Optional<Saldo> findTopByUsuarioOrderByActualizadoEnDesc(Usuario usuario);

    // ========== MÉTODO NUEVO CRÍTICO PARA ELIMINACIÓN DE USUARIOS ==========
    @Modifying
    @Transactional
    @Query("DELETE FROM Saldo s WHERE s.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Integer usuarioId);
    // =======================================================================

    // ========== MÉTODO NUEVO ADICIONAL PARA ELIMINACIÓN ==========
    @Modifying
    @Transactional
    @Query("DELETE FROM Saldo s WHERE s.condominio.id = :condominioId")
    void deleteByCondominioId(@Param("condominioId") Integer condominioId);
    // =============================================================

    // Métodos para FinanzasService
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

    // NUEVO MÉTODO para FinanzasController
    @Query("SELECT s FROM Saldo s WHERE " +
           "(:condominioId IS NULL OR s.usuario.condominio.id = :condominioId) " +
           "AND (:usuarioId IS NULL OR s.usuario.id = :usuarioId)")
    List<Saldo> findByCondominioIdAndUsuarioId(@Param("condominioId") Long condominioId,
                                               @Param("usuarioId") Long usuarioId);

    // Método para PagoController
    @Query("SELECT s FROM Saldo s WHERE s.pagado = false AND s.usuario = :usuario ORDER BY s.fechaLimite ASC")
    List<Saldo> findSaldosPendientesByUsuario(@Param("usuario") Usuario usuario);

    // MÉTODO FALTANTE - AGREGAR ESTE (para UsuarioController)
    @Query("SELECT s FROM Saldo s WHERE s.usuario.id = :usuarioId")
    List<Saldo> findByUsuarioId(@Param("usuarioId") Integer usuarioId);

    // MÉTODO PARA BUSCAR SALDOS POR PAGO PROGRAMADO
    @Query("SELECT s FROM Saldo s WHERE s.pagoProgramadoId = :pagoProgramadoId")
    List<Saldo> findByPagoProgramadoId(@Param("pagoProgramadoId") Integer pagoProgramadoId);

    // MÉTODO PARA VERIFICAR SI YA EXISTE UN SALDO RECURRENTE
    @Query("SELECT s FROM Saldo s WHERE s.usuario = :usuario AND s.pagoProgramadoId = :pagoProgramadoId AND s.numeroRepeticion = :numeroRepeticion")
    List<Saldo> findByUsuarioAndPagoProgramadoIdAndNumeroRepeticion(
            @Param("usuario") Usuario usuario,
            @Param("pagoProgramadoId") Integer pagoProgramadoId,
            @Param("numeroRepeticion") Integer numeroRepeticion);

    // MÉTODO PARA CONTAR SALDOS PENDIENTES DE UN PAGO PROGRAMADO
    @Query("SELECT COUNT(s) FROM Saldo s WHERE s.pagoProgramadoId = :pagoProgramadoId AND s.pagado = false")
    Long countByPagoProgramadoIdAndPendientes(@Param("pagoProgramadoId") Integer pagoProgramadoId);

    @Query("SELECT s FROM Saldo s WHERE s.usuario.condominio.id = :condominioId")
    List<Saldo> findByCondominioId(@Param("condominioId") Integer condominioId);

    // MÉTODOS NUEVOS PARA FILTRAR POR USUARIO Y CONDOMINIO
    @Query("SELECT s FROM Saldo s WHERE s.usuario = :usuario AND s.condominio = :condominio AND s.pagado = false ORDER BY s.fechaLimite ASC")
    List<Saldo> findByUsuarioAndCondominioAndPagadoFalse(@Param("usuario") Usuario usuario,
                                                         @Param("condominio") Condominio condominio);

    @Query("SELECT s FROM Saldo s WHERE s.usuario.id = :usuarioId AND s.condominio.id = :condominioId AND s.pagado = false ORDER BY s.fechaLimite ASC")
    List<Saldo> findByUsuarioIdAndCondominioIdAndPagadoFalse(@Param("usuarioId") Integer usuarioId,
                                                             @Param("condominioId") Integer condominioId);
}