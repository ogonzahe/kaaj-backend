package com.kaaj.repository;

import com.kaaj.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r WHERE r.usuarioId = :usuarioId AND r.fechaReserva >= CURRENT_DATE ORDER BY r.fechaReserva ASC, r.horaReserva ASC")
    List<Reserva> findProximasReservasPorUsuario(Integer usuarioId);

}