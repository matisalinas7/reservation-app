package com.devsenior.msal.reservation.dto.request;

import com.devsenior.msal.reservation.entity.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServicioRequestDTO(
        @NotBlank(message = "El nombre del Servicio no puede estar vacío")
        @Schema(example = "Corte")
        String nombre,

        @NotBlank(message = "La descripción del Servicio no puede estar vacío")
        @Schema(example = "Corte de cabello")
        String descripcion,

        @NotNull(message = "La duración del Servicio no puede estar vacía")
        @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
        @Schema(example = "30")
        Integer duracion,

        @NotNull(message = "La categoría no puede estar vacía")
        @Schema(example = "1")
        Long categoriaId
) {
}
