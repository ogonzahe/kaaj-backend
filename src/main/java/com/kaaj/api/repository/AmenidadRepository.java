package com.kaaj.api.repository;

import com.kaaj.api.model.Amenidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AmenidadRepository extends JpaRepository<Amenidad, Integer> {
    // ESTE ES EL MÉTODO IMPORTANTE QUE FALTABA
    List<Amenidad> findByCondominioId(Integer condominioId);

    // Otros métodos auxiliares
    List<Amenidad> findByEstadoOrderByNombreAsc(Amenidad.EstadoAmenidad estado);
    Amenidad findByNombre(String nombre);
}