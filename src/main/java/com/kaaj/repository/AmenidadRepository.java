package com.kaaj.repository;

import com.kaaj.model.Amenidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AmenidadRepository extends JpaRepository<Amenidad, Long> {

}