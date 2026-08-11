package com.devsenior.msal.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UsuarioRequestDTO(

        @NotNull(message = "El nombre no puede estar vacío")
        @Schema(example = "Juan")
        String nombre,

        @NotNull(message = "El apellido no puede estar vacío")
        @Schema(example = "Doe")
        String apellido,

        @NotBlank
        @Email(message = "El mail debe tener un formato válido")
        @Schema(example = "juandoe@gmail.com")
        String mail,

        @NotBlank
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Schema(example = "Password123")
        String contrasenia,

        @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "El teléfono debe tener un formato válido")
        @Schema(example = "+5491112345678")
        String telefono
) {}
