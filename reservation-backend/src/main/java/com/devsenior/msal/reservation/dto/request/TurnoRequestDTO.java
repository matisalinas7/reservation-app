package com.devsenior.msal.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TurnoRequestDTO(

        @NotNull(message = "El horario no puede estar vacío")
        @Schema(example = "1")
        Long horarioId,

        @NotNull(message = "La fecha no puede estar vacía: AA/DD/MM")
        @Schema(example = "2026-08-10")
        LocalDate fecha
) {}
