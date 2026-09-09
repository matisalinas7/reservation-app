package com.devsenior.msal.reservation.security;

import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByMail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con mail: " + username));
        return new UserDetailsImpl(usuario);
    }
}
