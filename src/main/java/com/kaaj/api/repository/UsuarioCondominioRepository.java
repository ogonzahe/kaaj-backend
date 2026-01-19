package com.kaaj.api.repository;

import com.kaaj.api.model.UsuarioCondominio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioCondominioRepository extends JpaRepository<UsuarioCondominio, Integer> {

    List<UsuarioCondominio> findByUsuarioId(Integer usuarioId);

    List<UsuarioCondominio> findByCondominioId(Integer condominioId);

    void deleteByUsuarioId(Integer usuarioId);

    UsuarioCondominio findByUsuarioIdAndCondominioId(Integer usuarioId, Integer condominioId);

    List<UsuarioCondominio> findByUsuarioIdAndEsPrincipalTrue(Integer usuarioId);
}