package com.kaaj.api.dto;

import com.kaaj.api.model.Usuario;
import com.kaaj.api.model.Saldo;
import com.kaaj.api.model.Notificacion;
import com.kaaj.api.model.Reserva;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PanelResponse {
    // Campos para el dashboard del usuario
    private Usuario usuario;
    private List<Saldo> saldos;
    private List<Notificacion> notificaciones;
    private List<Reserva> reservas;

    // Campos originales para compatibilidad (si se usan en algún lugar)
    private BigDecimal saldo;
    private LocalDate fechaLimite;
    private List<String> notificacionesSimples;
    private String amenidad;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private String rol;
}
