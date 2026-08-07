package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Turno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TurnoResponseDTO(
        Long id,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        LocalDateTime fechaBaja,
        Long horarioId
) {
    public static TurnoResponseDTO from(Turno turno) {
        return new TurnoResponseDTO(
                turno.getId(),
                turno.getFecha(),
                turno.getHoraInicio(),
                turno.getHoraFin(),
                turno.getFechaBaja(),
                turno.getHorario().getId()
        );
    }
}
