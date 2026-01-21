package com.kaaj.api.repository;

import com.kaaj.api.model.MantenimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<MantenimientoEntity, Integer> {

    // Método para obtener todos los reportes ordenados por fecha descendente
    @Query("SELECT m FROM MantenimientoEntity m ORDER BY m.fechaAlta DESC")
    List<MantenimientoEntity> findAllOrderByFechaAltaDesc();

    // Buscar por ID de condominio
    List<MantenimientoEntity> findByCondominioId(Integer condominioId);

    // Buscar por ID de usuario
    List<MantenimientoEntity> findByUsuarioId(Integer usuarioId);

    // Buscar por estatus
    List<MantenimientoEntity> findByEstatus_IdEstatus(Integer idEstatus);

    // Buscar por tipo
    List<MantenimientoEntity> findByTipo_IdTipo(Integer idTipo);
}