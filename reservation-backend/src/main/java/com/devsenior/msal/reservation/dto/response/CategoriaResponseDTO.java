package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Categoria;

import java.time.LocalDateTime;

public record CategoriaResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        LocalDateTime fechaBaja
) {
    public static CategoriaResponseDTO from(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getFechaBaja()
        );
    }
}
