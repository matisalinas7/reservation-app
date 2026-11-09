package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.entity.Usuario;

// Datos mínimos de un cliente para que el staff lo elija al reservar.
// Sin teléfono ni rol: el empleado no necesita más que esto.
public record ClienteResumenDTO(
        Long id,
        String nombre,
        String apellido,
        String mail
) {
    public static ClienteResumenDTO from(Usuario usuario) {
        return new ClienteResumenDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getMail()
        );
    }
}
