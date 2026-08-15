package com.devsenior.msal.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReservaRequestDTO(

        @NotNull(message = "El usuario no puede estar vacío")
        @Schema(example = "1")
        Long usuarioId,

        @NotNull(message = "El turno no puede estar vacío")
        @Schema(example = "1")
        Long turnoId,

        @NotNull(message = "El servicio no puede estar vacío")
        @Schema(example = "1")
        Long servicioId

) {}
