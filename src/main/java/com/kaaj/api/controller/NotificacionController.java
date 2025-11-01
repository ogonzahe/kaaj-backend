package com.kaaj.api.controller;

import com.kaaj.api.dto.NotificacionDTO;
import com.kaaj.api.mapper.NotificacionMapper;
import com.kaaj.api.model.Notificacion;
import com.kaaj.api.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> obtenerTodas() {
        List<NotificacionDTO> notificaciones = notificacionRepository.findAll()
                .stream()
                .map(NotificacionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtenerPorId(@PathVariable Integer id) {
        return notificacionRepository.findById(id)
                .map(NotificacionMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/leida")
    public ResponseEntity<String> marcarComoLeida(@PathVariable Integer id) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    notificacion.setLeida(true);
                    notificacionRepository.save(notificacion);
                    return ResponseEntity.ok("Notificación marcada como leída.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/leidas/todas")
    public ResponseEntity<String> marcarTodasComoLeidas() {
        List<Notificacion> todas = notificacionRepository.findAll();
        todas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(todas);
        return ResponseEntity.ok("Todas las notificaciones marcadas como leídas.");
    }
}
