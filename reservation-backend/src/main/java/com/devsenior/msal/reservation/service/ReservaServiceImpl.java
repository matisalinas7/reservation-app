package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.entity.Servicio;
import com.devsenior.msal.reservation.entity.Turno;
import com.devsenior.msal.reservation.entity.Usuario;
import com.devsenior.msal.reservation.enums.MotivoCancelacion;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.ReservaRepository;
import com.devsenior.msal.reservation.repository.ServicioRepository;
import com.devsenior.msal.reservation.repository.TurnoRepository;
import com.devsenior.msal.reservation.repository.UsuarioRepository;
import com.devsenior.msal.reservation.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private ReservaRepository reservaRepository;
    private UsuarioRepository  usuarioRepository;
    private TurnoRepository turnoRepository;
    private ServicioRepository servicioRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, TurnoRepository turnoRepository, ServicioRepository servicioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.turnoRepository = turnoRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    public ReservaResponseDTO crearReserva(ReservaRequestDTO request, UserDetailsImpl userDetails) {

        // La identidad sale del token: un CLIENTE solo puede reservar para sí mismo
        Usuario autenticado = userDetails.getUsuario();
        if (autenticado.getRol() == Rol.CLIENTE && !autenticado.getId().equals(request.usuarioId())) {
            throw new AccessDeniedException("Un cliente solo puede crear reservas a su nombre.");
        }

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Usuario no encontrado con id: " + request.usuarioId(),
                        HttpStatus.NOT_FOUND));

        if (usuario.getRol() != Rol.CLIENTE) {
            throw new BusinessRuleViolationException(
                    "Solo se pueden crear reservas para usuarios con rol CLIENTE.",
                    HttpStatus.BAD_REQUEST);
        }

        Servicio servicio = servicioRepository.findById(request.servicioId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Servicio no encontrado con id: " + request.servicioId(),
                        HttpStatus.NOT_FOUND));

        Turno turno = turnoRepository.findById(request.turnoId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Turno no encontrado con id: " + request.turnoId(),
                        HttpStatus.NOT_FOUND));

        if (!turno.getHorario().getServicio().getId().equals(request.servicioId())) {
            throw new BusinessRuleViolationException(
                    "El turno no corresponde al servicio seleccionado.",
                    HttpStatus.BAD_REQUEST);
        }

        if (reservaRepository.existsByTurnoAndEstado(turno, ReservationStatus.ACTIVE)) {
            throw new BusinessRuleViolationException(
                    "Ya existe una reserva activa para este turno.",
                    HttpStatus.CONFLICT);
            }

        ZoneId zonaArgentina = ZoneId.of("America/Argentina/Buenos_Aires");
        Instant ahora = Instant.now();
        Instant inicioTurno = LocalDateTime.of(
                turno.getFecha(),
                turno.getHoraInicio()
        ).atZone(zonaArgentina).toInstant();

        if (ahora.isAfter(inicioTurno)) {
            throw new BusinessRuleViolationException(
                    "No se puede reservar. El turno ya comenzó.",
                    HttpStatus.CONFLICT);
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setTurno(turno);
        reserva.setServicio(servicio);
        reserva.setEstado(ReservationStatus.ACTIVE);

        return ReservaResponseDTO.from(reservaRepository.save(reserva));
    }

    @Override
    public void cancelarReserva(Long id, MotivoCancelacion motivo, UserDetailsImpl userDetails) {

        Reserva reservaElegida = buscarReservaPorId(id);
        verificarAcceso(reservaElegida, userDetails);

        if (reservaElegida.getEstado() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleViolationException(
                    "Esta reserva ya está cancelada.",
                    HttpStatus.CONFLICT);
        }

        Rol rolAutenticado = userDetails.getUsuario().getRol();

        if (rolAutenticado == Rol.CLIENTE) {
            Integer duracionServicio = reservaElegida.getServicio().getDuracion();

            ZoneId zonaArgentina = ZoneId.of("America/Argentina/Buenos_Aires");
            Instant ahora = Instant.now();
            Instant inicioTurno = LocalDateTime.of(
                    reservaElegida.getTurno().getFecha(),
                    reservaElegida.getTurno().getHoraInicio()
            ).atZone(zonaArgentina).toInstant();


            Instant limiteCancelacion = inicioTurno.minusSeconds(duracionServicio * 60L);

            System.out.println("Ahora: " + ahora);
            System.out.println("Inicio turno: " + inicioTurno);
            System.out.println("Limite cancelacion: " + limiteCancelacion);

            if (ahora.isAfter(inicioTurno)) {
                throw new BusinessRuleViolationException(
                        "No se puede cancelar. El turno ya comenzó.",
                        HttpStatus.CONFLICT);
            }

            if (ahora.isAfter(limiteCancelacion)) {
                throw new BusinessRuleViolationException(
                        "No se puede cancelar. El plazo de cancelación ya venció.",
                        HttpStatus.CONFLICT);
            }
        }

        reservaElegida.setMotivoCancelacion(motivo);
        reservaElegida.setEstado(ReservationStatus.CANCELLED);
        reservaRepository.save(reservaElegida);
    }

    @Override
    public List<ReservaResponseDTO> findAllReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaResponseDTO::from)
                .toList();
    }

    @Override
    public List<ReservaResponseDTO> findReservasByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(ReservaResponseDTO::from)
                .toList();
    }

    @Override
    public ReservaResponseDTO findReservaById(Long id, UserDetailsImpl userDetails) {
        Reserva reserva = buscarReservaPorId(id);
        verificarAcceso(reserva, userDetails);
        return ReservaResponseDTO.from(reserva);
    }

    @Override
    public List<ReservaResponseDTO> findMisReservas(Long usuarioId) {
        return reservaRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(ReservaResponseDTO::from)
                .toList();
    }

    // Un CLIENTE solo ve y cancela sus reservas; para las ajenas responde 403.
    private void verificarAcceso(Reserva reserva, UserDetailsImpl userDetails) {
        Usuario autenticado = userDetails.getUsuario();
        if (autenticado.getRol() == Rol.CLIENTE
                && !reserva.getUsuario().getId().equals(autenticado.getId())) {
            throw new BusinessRuleViolationException(
                    "No tenés acceso a reservas de otro cliente.",
                    HttpStatus.FORBIDDEN);
        }
    }

    private Reserva buscarReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Reserva no encontrada con id: " + id,
                        HttpStatus.NOT_FOUND));
    }
}
