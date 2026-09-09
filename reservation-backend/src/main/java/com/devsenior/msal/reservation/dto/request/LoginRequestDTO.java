package com.devsenior.msal.reservation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank @Email String mail,
        @NotBlank String contrasenia
) {
}
