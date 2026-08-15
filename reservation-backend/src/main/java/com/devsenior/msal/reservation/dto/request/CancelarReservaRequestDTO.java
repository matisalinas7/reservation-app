package com.devsenior.msal.reservation.dto.request;

import com.devsenior.msal.reservation.enums.MotivoCancelacion;

public record CancelarReservaRequestDTO(
        MotivoCancelacion motivo
) {}
