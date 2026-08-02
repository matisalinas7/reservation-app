package com.devsenior.msal.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioRequestDTO(

        @NotNull(message = "El día de la semana no puede estar vacío")
        @Schema(example = "MONDAY")
        DayOfWeek diaSemana,

        @NotNull(message = "La hora de inicio no puede estar vacía")
        @Schema(example = "09:00:00")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin no puede estar vacía")
        @Schema(example = "10:00:00")
        LocalTime horaFin,

        @NotNull(message = "El servicio no puede estar vacío")
        @Schema(example = "1")
        Long servicioId
) {
}
