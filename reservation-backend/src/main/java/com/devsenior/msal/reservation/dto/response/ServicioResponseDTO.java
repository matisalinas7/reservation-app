package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Categoria;
import com.devsenior.msal.reservation.entity.Servicio;

import java.time.LocalDateTime;

public record ServicioResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        Integer duracion,
        LocalDateTime fechaBaja,
        Long categoriaId
) {
    public static ServicioResponseDTO from(Servicio servicio) {
        return new ServicioResponseDTO(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getDuracion(),
                servicio.getFechaBaja(),
                servicio.getCategoria().getId()
        );
    }
}
