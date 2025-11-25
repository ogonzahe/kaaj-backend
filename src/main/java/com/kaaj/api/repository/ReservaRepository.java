package com.kaaj.api.repository;

import com.kaaj.api.model.Reserva;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Reserva findTopByUsuarioOrderByFechaReservaAsc(Usuario usuario);

    @Query("SELECT r FROM Reserva r WHERE r.mes = :mes AND r.anio = :anio")
    List<Reserva> findByMesAndAnio(@Param("mes") Integer mes, @Param("anio") Integer anio);

    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario AND r.dia = :dia AND r.mes = :mes AND r.anio = :anio")
    Reserva findByUsuarioAndDiaAndMesAndAnio(
            @Param("usuario") Usuario usuario,
            @Param("dia") Integer dia,
            @Param("mes") Integer mes,
            @Param("anio") Integer anio);

    @Query("SELECT r FROM Reserva r WHERE r.amenidad = :amenidad AND r.dia = :dia AND r.mes = :mes AND r.anio = :anio AND r.horaInicio = :horaInicio")
    Reserva findByAmenidadAndDiaAndMesAndAnioAndHoraInicio(
            @Param("amenidad") String amenidad,
            @Param("dia") Integer dia,
            @Param("mes") Integer mes,
            @Param("anio") Integer anio,
            @Param("horaInicio") LocalTime horaInicio);
}