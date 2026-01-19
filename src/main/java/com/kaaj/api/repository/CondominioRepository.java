package com.kaaj.api.repository;

import com.kaaj.api.model.Condominio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CondominioRepository extends JpaRepository<Condominio, Integer> {
    
    // M?todos existentes
    List<Condominio> findAllByOrderByNombreAsc();
    
    // M?todos nuevos
    List<Condominio> findByActivo(Boolean activo);
    
    List<Condominio> findByEntidad(String entidad);
    
    List<Condominio> findByNombreContaining(String nombre);
    
    // M?todo para contar
    long count();
    
    // M?todo para encontrar por ciudad
    List<Condominio> findByCiudad(String ciudad);
}