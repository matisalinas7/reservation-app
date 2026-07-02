package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.entity.Categoria;

import java.util.List;

public interface CategoriaService {
    Categoria crearCategoria(Categoria categoria);
    Categoria actualizarCategoria(Long id, Categoria categoria);
    void eliminarCategoria(Long id);
    void reactivarCategoria(Long id);
    Categoria findCategoriaById(Long id);
    List<Categoria> findAllCategorias();
    List<Categoria> findCategoriasDisponibles();
}
