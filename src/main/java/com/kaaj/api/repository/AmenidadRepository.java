package com.kaaj.api.repository;

import com.kaaj.api.model.Amenidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AmenidadRepository extends JpaRepository<Amenidad, Integer> {

    List<Amenidad> findByCondominioId(Integer condominioId);

    List<Amenidad> findByEstadoOrderByNombreAsc(Amenidad.EstadoAmenidad estado);

    Amenidad findByNombre(String nombre);

    @Query("SELECT a FROM Amenidad a WHERE a.nombre = :nombre AND a.condominioId = :condominioId")
    Amenidad findByNombreAndCondominioId(@Param("nombre") String nombre, @Param("condominioId") Integer condominioId);
}