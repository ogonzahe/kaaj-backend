package com.kaaj.service;

import com.kaaj.api.dto.ProximaReservaDTO;
import com.kaaj.model.Reserva;
import com.kaaj.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public Optional<ProximaReservaDTO> getProximaReserva(Integer usuarioId) {
        List<Reserva> proximasReservas = reservaRepository.findProximasReservasPorUsuario(usuarioId);

        if (proximasReservas.isEmpty()) {
            return Optional.empty();
        }

        Reserva proximaReserva = proximasReservas.get(0);

        ProximaReservaDTO dto = new ProximaReservaDTO();
        dto.setNombreAmenidad(proximaReserva.getAmenidad());
        dto.setHorario(formatearHorario(proximaReserva));

        return Optional.of(dto);
    }

    private String formatearHorario(Reserva reserva) {
        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");

        String fechaFormateada;
        if (reserva.getFechaReserva().equals(hoy)) {
            fechaFormateada = "Hoy";
        } else if (reserva.getFechaReserva().equals(manana)) {
            fechaFormateada = "Mañana";
        } else {
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM");
            fechaFormateada = reserva.getFechaReserva().format(formatoFecha);
        }

        return fechaFormateada + ", " + reserva.getHoraReserva().format(formatoHora);
    }
}