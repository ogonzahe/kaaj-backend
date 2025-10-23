package com.kaaj.api.repository;

import com.kaaj.api.model.Reserva;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    Reserva findTopByUsuarioOrderByFechaReservaAsc(Usuario usuario);
}