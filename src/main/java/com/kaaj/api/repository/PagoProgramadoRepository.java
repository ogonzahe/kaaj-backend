package com.kaaj.api.repository;

import com.kaaj.api.model.PagoProgramado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoProgramadoRepository extends JpaRepository<PagoProgramado, Integer> {
    List<PagoProgramado> findByCondominioId(Integer condominioId);

    // AGREGAR ESTE MÉTODO - necesario para el Job de pagos recurrentes
    List<PagoProgramado> findByEsRecurrenteTrue();
}