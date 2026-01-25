package com.kaaj.api.repository;

import com.kaaj.api.model.AccesoSeguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccesoSeguridadRepository extends JpaRepository<AccesoSeguridad, Integer> {
    List<AccesoSeguridad> findByVisitaId(Integer visitaId);

    List<AccesoSeguridad> findTop50ByOrderByFechaHoraAccesoDesc();
}