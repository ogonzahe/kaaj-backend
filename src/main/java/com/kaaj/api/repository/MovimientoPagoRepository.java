package com.kaaj.api.repository;

import com.kaaj.api.model.MovimientoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoPagoRepository extends JpaRepository<MovimientoPago, Long> {

    List<MovimientoPago> findByPropietarioId(Long propietarioId);

    @Query("SELECT mp FROM MovimientoPago mp WHERE mp.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin")
    List<MovimientoPago> findByFechaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}