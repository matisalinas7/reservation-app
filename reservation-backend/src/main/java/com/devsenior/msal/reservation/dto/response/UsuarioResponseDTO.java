package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.enums.Rol;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String mail,
        String telefono,
        Rol rol,
        LocalDateTime fechaBaja
) {
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getMail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getFechaBaja()
        );
    }
}
