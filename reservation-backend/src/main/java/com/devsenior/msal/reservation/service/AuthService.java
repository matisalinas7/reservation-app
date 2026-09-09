package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.LoginRequestDTO;
import com.devsenior.msal.reservation.dto.request.RegisterRequestDTO;
import com.devsenior.msal.reservation.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}
