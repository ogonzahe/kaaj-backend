package com.kaaj.api.repository; // AJUSTADO

import com.kaaj.api.model.Reporte; // IMPORT AJUSTADO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByUsuarioId(Integer usuarioId);
}