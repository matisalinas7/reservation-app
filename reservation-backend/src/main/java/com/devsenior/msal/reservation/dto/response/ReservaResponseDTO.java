package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.enums.MotivoCancelacion;
import com.devsenior.msal.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        Long usuarioId,
        Long turnoId,
        Long servicioId,
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
                reserva.getEstado(),
                reserva.getMotivoCancelacion(),
                reserva.getFechaBaja()
        );
    }
}
