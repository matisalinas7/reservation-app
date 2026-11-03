package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.enums.MotivoCancelacion;
import com.devsenior.msal.reservation.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaResponseDTO(
        Long id,
        Long usuarioId,
        Long turnoId,
        Long servicioId,
        String servicioNombre,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        ReservationStatus estado,
        MotivoCancelacion motivoCancelacion,
        LocalDateTime fechaBaja
) {
    public static ReservaResponseDTO from(Reserva reserva){
        return new  ReservaResponseDTO(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getTurno().getId(),
                reserva.getServicio().getId(),
                reserva.getServicio().getNombre(),
                reserva.getTurno().getFecha(),
                reserva.getTurno().getHoraInicio(),
                reserva.getTurno().getHoraFin(),
                reserva.getEstado(),
                reserva.getMotivoCancelacion(),
                reserva.getFechaBaja()
        );
    }
}
