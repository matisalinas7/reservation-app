package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.TurnoRequestDTO;
import com.devsenior.msal.reservation.dto.response.TurnoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface TurnoService {
    List<TurnoResponseDTO> generarTurno(TurnoRequestDTO request);
    void eliminarTurno(Long id);
    void reactivarTurno(Long id);
    TurnoResponseDTO findTurnoById(Long id);
    List<TurnoResponseDTO> findAllTurnos();
    List<TurnoResponseDTO> findTurnosDisponibles();
    List<TurnoResponseDTO> findTurnosByServicioAndFecha(Long servicioId, LocalDate fecha);
}
