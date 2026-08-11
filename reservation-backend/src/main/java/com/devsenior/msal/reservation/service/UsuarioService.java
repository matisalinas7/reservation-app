package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.UsuarioRequestDTO;
import com.devsenior.msal.reservation.dto.response.UsuarioResponseDTO;
import com.devsenior.msal.reservation.enums.Rol;

import java.util.List;

public interface UsuarioService {
    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO request);
    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO request);
    void eliminarUsuario(Long id);
    void reactivarUsuario(Long id);
    void actualizarRol(Long id, Rol rol);
    UsuarioResponseDTO findUsuarioById(Long id);
    List<UsuarioResponseDTO> findAllUsuarios();
    List<UsuarioResponseDTO> findAllUsuariosActivos();
}
