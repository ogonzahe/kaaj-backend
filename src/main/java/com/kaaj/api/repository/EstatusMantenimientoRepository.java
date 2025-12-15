package com.kaaj.api.repository;

import com.kaaj.api.model.EstatusMantenimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstatusMantenimientoRepository extends JpaRepository<EstatusMantenimientoEntity, Integer> {
}