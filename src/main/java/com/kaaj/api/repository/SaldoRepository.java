package com.kaaj.api.repository;

import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SaldoRepository extends JpaRepository<Saldo, Integer> {

    // Método que estás usando en UsuarioController
    Saldo findTopByUsuarioOrderByActualizadoEnDesc(Usuario usuario);

    // Encuentra saldos pendientes (no pagados) de un usuario
    @Query("SELECT s FROM Saldo s WHERE s.usuario = :usuario AND (s.pagado = false OR s.pagado IS NULL) ORDER BY s.actualizadoEn DESC")
    List<Saldo> findSaldosPendientesByUsuario(@Param("usuario") Usuario usuario);

    // Encuentra todos los saldos de un usuario
    List<Saldo> findByUsuarioOrderByActualizadoEnDesc(Usuario usuario);

    // Método alternativo si el anterior falla
    @Query("SELECT s FROM Saldo s WHERE s.usuario = :usuario ORDER BY s.actualizadoEn DESC")
    List<Saldo> findByUsuario(@Param("usuario") Usuario usuario);
}