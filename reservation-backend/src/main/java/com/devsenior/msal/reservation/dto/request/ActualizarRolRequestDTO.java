package com.devsenior.msal.reservation.dto.request;

import com.devsenior.msal.reservation.enums.Rol;
import jakarta.validation.constraints.NotNull;

public record ActualizarRolRequestDTO(
        @NotNull(message = "El rol no puede estar vacío")
        Rol rol
) {}
