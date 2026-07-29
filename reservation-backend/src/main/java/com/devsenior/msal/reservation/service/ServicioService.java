package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.CategoriaRequestDTO;
import com.devsenior.msal.reservation.dto.request.ServicioRequestDTO;
import com.devsenior.msal.reservation.dto.response.ServicioResponseDTO;

import java.util.List;

public interface ServicioService {
    ServicioResponseDTO crearServicio(ServicioRequestDTO request);
    void eliminarServicio(Long id);
    ServicioResponseDTO actualizarServicio(Long id, ServicioRequestDTO request);
    void reactivarServicio(Long id);
    ServicioResponseDTO findServicioById(Long id);
    List<ServicioResponseDTO> findAllServicios();
    List<ServicioResponseDTO> findServiciosDisponibles();
    List<ServicioResponseDTO> findServicioByCategoriaId(Long  categoriaId);
}