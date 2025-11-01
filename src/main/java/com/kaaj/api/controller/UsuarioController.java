package com.kaaj.api.controller;

import com.kaaj.api.dto.LoginRequest;
import com.kaaj.api.dto.PanelResponse;
import com.kaaj.api.model.*;
import com.kaaj.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private SaldoRepository saldoRepo;

    @Autowired
    private NotificacionRepository notiRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepo.findByCorreoAndContrasena(request.getCorreo(), request.getContrasena());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        Saldo saldo = saldoRepo.findTopByUsuarioOrderByActualizadoEnDesc(usuario);

        List<Notificacion> notificaciones = notiRepo.findAll()
                .stream()
                .filter(n -> n.getLeida() != null && !n.getLeida())
                .toList();

        Reserva reserva = reservaRepo.findTopByUsuarioOrderByFechaReservaAsc(usuario);

        PanelResponse response = new PanelResponse(
                saldo != null ? saldo.getMonto() : null,
                saldo != null ? saldo.getFechaLimite() : null,
                notificaciones.stream().map(Notificacion::getTitulo).toList(),
                reserva != null ? reserva.getAmenidad() : null,
                reserva != null ? reserva.getFechaReserva() : null,
                reserva != null ? reserva.getHoraReserva() : null);

        return ResponseEntity.ok(response);
    }
}
