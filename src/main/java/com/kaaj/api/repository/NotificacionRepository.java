package com.kaaj.api.repository;

import com.kaaj.api.model.Notificacion;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);

    // MÉTODO NUEVO AGREGADO
    List<Notificacion> findByUsuario(Usuario usuario);

    List<Notificacion> findAllByOrderByCreadaEnDesc();

    // MÉTODOS FALTANTES - AGREGAR ESTOS (para UsuarioController)
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leida = false")
    List<Notificacion> findByUsuarioIdAndLeidoFalse(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId")
    List<Notificacion> findByUsuarioId(@Param("usuarioId") Integer usuarioId);

    // ========== MÉTODO NUEVO CRÍTICO PARA ELIMINACIÓN DE USUARIOS ==========
    @Modifying
    @Transactional
    @Query("DELETE FROM Notificacion n WHERE n.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Integer usuarioId);
    // =======================================================================

    // ========== MÉTODOS NUEVOS PARA SOPORTAR condominio_id ==========

    // 1. Buscar por condominio_id
    @Query("SELECT n FROM Notificacion n WHERE n.condominio.id = :condominioId")
    List<Notificacion> findByCondominioId(@Param("condominioId") Integer condominioId);

    // 2. Buscar por múltiples condominios
    @Query("SELECT n FROM Notificacion n WHERE n.condominio.id IN :condominiosIds")
    List<Notificacion> findByCondominioIdIn(@Param("condominiosIds") List<Integer> condominiosIds);

    // 3. Buscar por usuario y condominio
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.condominio.id = :condominioId")
    List<Notificacion> findByUsuarioIdAndCondominioId(@Param("usuarioId") Integer usuarioId,
                                                     @Param("condominioId") Integer condominioId);

    // 4. Buscar notificaciones no leídas por condominio
    @Query("SELECT n FROM Notificacion n WHERE n.condominio.id = :condominioId AND n.leida = false")
    List<Notificacion> findByCondominioIdAndLeidaFalse(@Param("condominioId") Integer condominioId);

    // 5. Buscar notificaciones sin usuario específico (para todo el condominio)
    @Query("SELECT n FROM Notificacion n WHERE n.condominio.id = :condominioId AND n.usuario IS NULL")
    List<Notificacion> findByCondominioIdAndUsuarioIsNull(@Param("condominioId") Integer condominioId);

    // 6. Buscar todas las notificaciones de un condominio ordenadas por fecha
    @Query("SELECT n FROM Notificacion n WHERE n.condominio.id = :condominioId ORDER BY n.creadaEn DESC")
    List<Notificacion> findByCondominioIdOrderByCreadaEnDesc(@Param("condominioId") Integer condominioId);

    // 7. Contar notificaciones no leídas por condominio
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.condominio.id = :condominioId AND n.leida = false")
    Long countByCondominioIdAndLeidaFalse(@Param("condominioId") Integer condominioId);
}