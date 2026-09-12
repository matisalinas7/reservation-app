package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.enums.MotivoCancelacion;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReservaService {
    ReservaResponseDTO crearReserva(ReservaRequestDTO request);
    void cancelarReserva(Long id, MotivoCancelacion motivo, Authentication authentication);
    List<ReservaResponseDTO> findAllReservas();
    List<ReservaResponseDTO> findReservasByUsuarioId(Long usuarioId);
    ReservaResponseDTO findReservaById(Long id);
    List<ReservaResponseDTO> findMisReservas(Long usuarioId);
}
