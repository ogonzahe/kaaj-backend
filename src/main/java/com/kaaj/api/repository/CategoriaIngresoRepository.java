package com.kaaj.api.repository;

import com.kaaj.api.model.CategoriaIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaIngresoRepository extends JpaRepository<CategoriaIngreso, Long> {
    List<CategoriaIngreso> findByCondominioId(Long condominioId);
}