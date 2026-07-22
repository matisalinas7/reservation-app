package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.CategoriaRequestDTO;
import com.devsenior.msal.reservation.dto.response.CategoriaResponseDTO;
import com.devsenior.msal.reservation.entity.Categoria;

import java.util.List;

public interface CategoriaService {
    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request);
    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO request);
    void eliminarCategoria(Long id);
    void reactivarCategoria(Long id);
    CategoriaResponseDTO findCategoriaById(Long id);
    List<CategoriaResponseDTO> findAllCategorias();
    List<CategoriaResponseDTO> findCategoriasDisponibles();
}
