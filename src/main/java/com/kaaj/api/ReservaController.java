package com.kaaj.api;

import com.kaaj.api.dto.ProximaReservaDTO;
import com.kaaj.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/proxima")
    public ResponseEntity<ProximaReservaDTO> getProximaReserva() {
        // NOTA: Usamos 1L para pruebas. Esto se debe cambiar por el ID del usuario
        // autenticado.
        Integer usuarioId = 1;

        Optional<ProximaReservaDTO> dto = reservaService.getProximaReserva(usuarioId);

        return dto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}