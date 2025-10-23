package com.kaaj.api.repository;

import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByCorreoAndContrasena(String correo, String contrasena);
}