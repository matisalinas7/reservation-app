package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.UsuarioRequestDTO;
import com.devsenior.msal.reservation.dto.response.UsuarioResponseDTO;
import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.ReservaRepository;
import com.devsenior.msal.reservation.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setMail(request.mail());
        usuario.setContrasenia(request.contrasenia());
        usuario.setTelefono(request.telefono());

        usuario.setRol(Rol.CLIENTE);

        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO request) {
        Usuario usuarioExistente = buscarUsuarioPorId(id);

        usuarioExistente.setNombre(request.nombre());
        usuarioExistente.setApellido(request.apellido());
        usuarioExistente.setMail(request.mail());
        usuarioExistente.setContrasenia(request.contrasenia());
        usuarioExistente.setTelefono(request.telefono());

        return UsuarioResponseDTO.from(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuarioElegido =  buscarUsuarioPorId(id);
        if(usuarioElegido.getFechaBaja() != null) {
            throw new BusinessRuleViolationException(
                    "Este usuario ya está dado de baja",
                    HttpStatus.CONFLICT);
        }
        usuarioElegido.setFechaBaja(LocalDateTime.now());
        usuarioRepository.save(usuarioElegido);

        List<Reserva> reservasAfectadas = reservaRepository
                .findByUsuarioAndEstadoAndTurno_FechaAfter(
                        usuarioElegido,
                        ReservationStatus.ACTIVE,
                        LocalDate.now()
                );
        reservasAfectadas.forEach(reserva -> reserva.setEstado(ReservationStatus.CANCELLED));
        reservaRepository.saveAll(reservasAfectadas);
    }

    @Override
    public void reactivarUsuario(Long id) {
        Usuario usuarioElegido =  buscarUsuarioPorId(id);
        if(usuarioElegido.getFechaBaja() == null) {
            throw new BusinessRuleViolationException(
                    "Este usuario está activo, no se puede reactivar",
                    HttpStatus.CONFLICT);
        }
        usuarioElegido.setFechaBaja(null);
        usuarioRepository.save(usuarioElegido);
    }

    public void actualizarRol(Long id, Rol rol) {
        Usuario usuarioElegido =  buscarUsuarioPorId(id);
        usuarioElegido.setRol(rol);
        usuarioRepository.save(usuarioElegido);
    }

    @Override
    public UsuarioResponseDTO findUsuarioById(Long id) {
        return UsuarioResponseDTO.from(buscarUsuarioPorId(id));
    }

    @Override
    public List<UsuarioResponseDTO> findAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    @Override
    public List<UsuarioResponseDTO> findAllUsuariosActivos() {
        return usuarioRepository.findByFechaBajaIsNull()
                .stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).
                orElseThrow(() -> new BusinessRuleViolationException(
                        "Usuario no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND));
    }
}
