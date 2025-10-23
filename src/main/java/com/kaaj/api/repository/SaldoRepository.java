package com.kaaj.api.repository;

import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoRepository extends JpaRepository<Saldo, Integer> {
    Saldo findTopByUsuarioOrderByActualizadoEnDesc(Usuario usuario);
}