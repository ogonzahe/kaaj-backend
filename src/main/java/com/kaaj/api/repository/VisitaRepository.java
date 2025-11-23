package com.kaaj.api.repository;

import com.kaaj.api.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VisitaRepository extends JpaRepository<Visita, Integer> {
    Optional<Visita> findByCodigoQr(String codigoQr);

    List<Visita> findByUsuarioIdOrderByCreadoEnDesc(Integer usuarioId);

    List<Visita> findByEstado(Visita.EstadoVisita estado);
}
