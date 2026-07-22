package com.devsenior.msal.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(
        @NotBlank(message = "El nombre de la Categoría no puede estar vacío")
        @Schema(example = "Peluquería")
        String nombre,

        @NotBlank(message = "La descripción de la Categoría no puede estar vacía")
        @Schema(example = "Servicios de corte y coloración")
        String descripcion
){}
