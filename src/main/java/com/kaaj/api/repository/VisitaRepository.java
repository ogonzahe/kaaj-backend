// src/main/java/com/kaaj/api/repository/VisitaRepository.java
package com.kaaj.api.repository;

import com.kaaj.api.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitaRepository extends JpaRepository<Visita, Integer> {
    Optional<Visita> findByCodigoQr(String codigoQr);
    List<Visita> findByUsuarioIdOrderByCreadoEnDesc(Integer usuarioId);
    
    // Método para buscar visitas por fecha programada
    List<Visita> findByFechaProgramada(LocalDate fechaProgramada);
    
    // Método alternativo usando query personalizada si el anterior no funciona
    @Query("SELECT v FROM Visita v WHERE v.fechaProgramada = :fecha")
    List<Visita> findVisitasByFecha(@Param("fecha") LocalDate fecha);
    
    // Método para buscar visitas del día actual
    @Query("SELECT v FROM Visita v WHERE v.fechaProgramada = CURRENT_DATE")
    List<Visita> findVisitasHoy();
    
    // Método para buscar visitas pendientes del día
    @Query("SELECT v FROM Visita v WHERE v.fechaProgramada = :fecha AND v.estado = 'Generado'")
    List<Visita> findVisitasPendientesByFecha(@Param("fecha") LocalDate fecha);
}