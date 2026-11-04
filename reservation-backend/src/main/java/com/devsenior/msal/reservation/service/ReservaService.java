package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.enums.MotivoCancelacion;
import com.devsenior.msal.reservation.security.UserDetailsImpl;

import java.util.List;

public interface ReservaService {
    ReservaResponseDTO crearReserva(ReservaRequestDTO request, UserDetailsImpl userDetails);
    void cancelarReserva(Long id, MotivoCancelacion motivo, UserDetailsImpl userDetails);
    List<ReservaResponseDTO> findAllReservas();
    List<ReservaResponseDTO> findReservasByUsuarioId(Long usuarioId);
    ReservaResponseDTO findReservaById(Long id, UserDetailsImpl userDetails);
    List<ReservaResponseDTO> findMisReservas(Long usuarioId);
}
