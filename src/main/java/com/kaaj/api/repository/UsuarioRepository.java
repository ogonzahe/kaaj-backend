package com.kaaj.api.repository;

import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByCorreo(String correo);

    Optional<Usuario> findOneByCorreo(String correo);

    @Query("SELECT u FROM Usuario u WHERE u.correo = :correo AND u.contrasena = :contrasena")
    Usuario findByCorreoAndContrasena(@Param("correo") String correo, @Param("contrasena") String contrasena);

    // Buscar usuarios por ID del condominio (usando condominio.id)
    @Query("SELECT u FROM Usuario u WHERE u.condominio.id = :condominioId")
    List<Usuario> findByCondominioId(@Param("condominioId") Integer condominioId);

    // Buscar usuarios por rol y condominio
    @Query("SELECT u FROM Usuario u WHERE u.rolNombre = :rolNombre AND u.condominio.id = :condominioId")
    List<Usuario> findByRolNombreAndCondominioId(@Param("rolNombre") String rolNombre, @Param("condominioId") Integer condominioId);

    // Buscar administradores de un condominio
    @Query("SELECT u FROM Usuario u WHERE u.rolNombre = 'admin_usuario' AND u.condominio.id = :condominioId")
    List<Usuario> findAdminsByCondominio(@Param("condominioId") Integer condominioId);

    // Buscar residentes de un condominio
    @Query("SELECT u FROM Usuario u WHERE u.rolNombre = 'USUARIO' AND u.condominio.id = :condominioId")
    List<Usuario> findResidentesByCondominio(@Param("condominioId") Integer condominioId);

    // Buscar usuarios por nombre o correo (método faltante)
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Usuario> findByNombreContainingOrCorreoContaining(@Param("query") String query);

    // MÉTODOS FALTANTES - AGREGAR ESTOS:

    // Método para verificar si existe un correo
    boolean existsByCorreo(String correo);

    // Método para buscar usuarios por rol
    @Query("SELECT u FROM Usuario u WHERE u.rolNombre = :rolNombre")
    List<Usuario> findByRolNombre(@Param("rolNombre") String rolNombre);
}