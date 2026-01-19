package com.kaaj.api.repository;

import com.kaaj.api.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByUsuarioId(Long usuarioId);

    List<Reporte> findByCondominioId(Long condominioId);

    List<Reporte> findByEstado(String estado);

    List<Reporte> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}