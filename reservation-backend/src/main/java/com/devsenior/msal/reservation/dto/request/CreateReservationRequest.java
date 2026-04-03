package com.devsenior.msal.reservation.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
        @NotBlank @Schema(example = "Juan Pérez") String customerName,
        @NotNull @Schema(example = "2026-03-28") LocalDate date,
        @NotNull @Schema(example = "08:00:00") LocalTime time,
        @NotBlank @Schema(example = "Fútbol 5") String service) {}
