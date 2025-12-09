package com.kaaj.api.repository;

import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Este método debe existir
    Usuario findByCorreo(String correo);

    // O también puedes usar Optional
    Optional<Usuario> findOneByCorreo(String correo);

    // Si necesitas buscar por correo y contraseña
    @Query("SELECT u FROM Usuario u WHERE u.correo = :correo AND u.contrasena = :contrasena")
    Usuario findByCorreoAndContrasena(@Param("correo") String correo, @Param("contrasena") String contrasena);
}