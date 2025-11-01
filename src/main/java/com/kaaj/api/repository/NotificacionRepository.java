package com.kaaj.api.repository;

import com.kaaj.api.model.Notificacion;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByUsuario(Usuario usuario);

    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);

    @Transactional
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario = :usuario AND n.leida = false")
    void marcarComoLeidas(@Param("usuario") Usuario usuario);

    List<Notificacion> findByLeidaFalse();
}
