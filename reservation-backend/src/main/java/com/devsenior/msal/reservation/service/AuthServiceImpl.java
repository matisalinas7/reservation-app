package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.LoginRequestDTO;
import com.devsenior.msal.reservation.dto.request.RegisterRequestDTO;
import com.devsenior.msal.reservation.dto.response.AuthResponseDTO;
import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.UsuarioRepository;
import com.devsenior.msal.reservation.security.JwtService;
import com.devsenior.msal.reservation.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.findByMail(request.mail()).isPresent()) {
            throw new BusinessRuleViolationException(
                    "Ya existe un usuario con ese mail.",
                    HttpStatus.CONFLICT);
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setMail(request.mail());
        usuario.setContrasenia(passwordEncoder.encode(request.contrasenia()));
        usuario.setTelefono(request.telefono());
        usuario.setRol(Rol.CLIENTE);

        usuarioRepository.save(usuario);

        UserDetails userDetails = new UserDetailsImpl(usuario);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(token, usuario.getMail(), usuario.getRol());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.mail(),
                        request.contrasenia()
                )
        );

        Usuario usuario = usuarioRepository.findByMail(request.mail())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Usuario no encontrado.",
                        HttpStatus.NOT_FOUND));

        UserDetails userDetails = new UserDetailsImpl(usuario);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(token, usuario.getMail(), usuario.getRol());
    }
}
