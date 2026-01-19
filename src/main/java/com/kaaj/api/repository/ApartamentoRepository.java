package com.kaaj.api.repository;

import com.kaaj.api.model.Apartamento;
import com.kaaj.api.model.Condominio;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ApartamentoRepository extends JpaRepository<Apartamento, Integer> {

    List<Apartamento> findByCondominio(Condominio condominio);

    List<Apartamento> findByCondominioId(Integer condominioId);

    List<Apartamento> findByPropietario(Usuario propietario);

    List<Apartamento> findByInquilino(Usuario inquilino);

    @Query("SELECT a FROM Apartamento a WHERE a.condominio.id = :condominioId AND a.edificio = :edificio AND a.numero = :numero")
    Optional<Apartamento> findByCondominioAndEdificioAndNumero(
            @Param("condominioId") Integer condominioId,
            @Param("edificio") String edificio,
            @Param("numero") String numero);

    List<Apartamento> findByEstaAlquilado(Boolean estaAlquilado);

    List<Apartamento> findByEstaHabitado(Boolean estaHabitado);

    @Query("SELECT COUNT(a) FROM Apartamento a WHERE a.condominio.id = :condominioId")
    Long countByCondominioId(@Param("condominioId") Integer condominioId);

    @Query("SELECT COUNT(a) FROM Apartamento a WHERE a.condominio.id = :condominioId AND a.estaHabitado = true")
    Long countHabitadosByCondominioId(@Param("condominioId") Integer condominioId);

    @Query("SELECT COUNT(a) FROM Apartamento a WHERE a.condominio.id = :condominioId AND a.estaAlquilado = true")
    Long countAlquiladosByCondominioId(@Param("condominioId") Integer condominioId);
}