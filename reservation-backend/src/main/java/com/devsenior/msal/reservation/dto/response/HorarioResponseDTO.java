package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Horario;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record HorarioResponseDTO(
        Long id,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        LocalDateTime fechaBaja,
        Long servicioId
) {
    public static HorarioResponseDTO from (Horario horario) {
        return new HorarioResponseDTO(
                horario.getId(),
                horario.getDiaSemana(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getFechaBaja(),
                horario.getServicio().getId()
        );
    }
}
