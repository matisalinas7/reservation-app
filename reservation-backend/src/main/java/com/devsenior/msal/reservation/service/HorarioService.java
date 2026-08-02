package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.HorarioRequestDTO;
import com.devsenior.msal.reservation.dto.response.HorarioResponseDTO;

import java.util.List;

public interface HorarioService {
    HorarioResponseDTO crearHorario(HorarioRequestDTO request);
    HorarioResponseDTO actualizarHorario(Long id, HorarioRequestDTO request);
    void eliminarHorario(Long id);
    void reactivarHorario(Long id);
    HorarioResponseDTO findHorarioById(Long id);
    List<HorarioResponseDTO> findAllHorarios();
    List<HorarioResponseDTO> findHorariosDisponibles();
    List<HorarioResponseDTO> findHorarioByServicioId(Long servicioId);
}
