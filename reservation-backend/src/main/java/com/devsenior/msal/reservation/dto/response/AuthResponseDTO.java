package com.devsenior.msal.reservation.dto.response;

import com.devsenior.msal.reservation.enums.Rol;

public record AuthResponseDTO(
        String token,
        Long id,
        String mail,
        Rol rol
) {}
