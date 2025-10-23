package com.kaaj.api.repository;

import com.kaaj.api.model.Notificacion;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);
}