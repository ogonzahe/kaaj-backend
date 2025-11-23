package com.kaaj.api.repository;

import com.kaaj.api.model.EscaneoQr;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EscaneoQrRepository extends JpaRepository<EscaneoQr, Integer> {
    List<EscaneoQr> findByVisitaIdOrderByFechaEscaneoDesc(Integer visitaId);
}