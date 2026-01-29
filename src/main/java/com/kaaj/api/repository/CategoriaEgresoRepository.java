package com.kaaj.api.repository;

import com.kaaj.api.model.CategoriaEgreso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaEgresoRepository extends JpaRepository<CategoriaEgreso, Long> {
    List<CategoriaEgreso> findByCondominioId(Long condominioId);
}