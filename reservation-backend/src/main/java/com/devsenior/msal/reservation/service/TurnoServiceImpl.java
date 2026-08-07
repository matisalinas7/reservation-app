package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.TurnoRequestDTO;
import com.devsenior.msal.reservation.dto.response.TurnoResponseDTO;
import com.devsenior.msal.reservation.entity.Horario;
import com.devsenior.msal.reservation.entity.Turno;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.HorarioRepository;
import com.devsenior.msal.reservation.repository.ReservaRepository;
import com.devsenior.msal.reservation.repository.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final ReservaRepository reservaRepository;
    private final HorarioRepository horarioRepository;

    public TurnoServiceImpl(TurnoRepository turnoRepository,  ReservaRepository reservaRepository, HorarioRepository horarioRepository) {
        this.turnoRepository = turnoRepository;
        this.reservaRepository = reservaRepository;
        this.horarioRepository = horarioRepository;
    }

    @Override
    public List<TurnoResponseDTO> generarTurno(TurnoRequestDTO request) {

        Horario horario = horarioRepository.findById(request.horarioId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Horario no encontrado con id: " + request.horarioId(),
                        HttpStatus.NOT_FOUND));

        Integer duracion = horario.getServicio().getDuracion();
        LocalTime horaActual = horario.getHoraInicio();
        List<Turno> turnos = new ArrayList<>();

        while (horaActual.plusMinutes(duracion).compareTo(horario.getHoraFin()) <= 0) {
                Turno turno = new Turno();
                turno.setFecha(request.fecha());
                turno.setHoraInicio(horaActual);
                turno.setHoraFin(horaActual.plusMinutes(duracion));
                turno.setHorario(horario);
                turnos.add(turno);
                horaActual = horaActual.plusMinutes(duracion);
        }
        return turnoRepository.saveAll(turnos)
                .stream()
                .map(TurnoResponseDTO::from)
                .toList();
    }

    @Transactional
    @Override
    public void eliminarTurno(Long id) {
        Turno turnoElegido = buscarTurnoPorId(id);
        if(turnoElegido.getFechaBaja() != null) {
            throw new BusinessRuleViolationException(
                    "Este turno ya está dado de baja",
                    HttpStatus.BAD_REQUEST);
        }
        turnoElegido.setFechaBaja(LocalDateTime.now());
        turnoRepository.save(turnoElegido);

        reservaRepository.findByTurnoAndEstado(turnoElegido, ReservationStatus.ACTIVE)
                .ifPresent(reserva -> {
                    reserva.setEstado(ReservationStatus.CANCELLED);
                    reservaRepository.save(reserva);
                });
    }

    @Override
    public void reactivarTurno(Long id) {
        Turno turnoElegido = buscarTurnoPorId(id);
        if(turnoElegido.getFechaBaja() == null) {
            throw new BusinessRuleViolationException(
                    "Este turno está activo, no se puede reactivar.",
                    HttpStatus.CONFLICT);
        }
        turnoElegido.setFechaBaja(null);
        turnoRepository.save(turnoElegido);
    }

    @Override
    public TurnoResponseDTO findTurnoById(Long id) {
        return TurnoResponseDTO.from(buscarTurnoPorId(id));
    }

    @Override
    public List<TurnoResponseDTO> findAllTurnos() {
        return turnoRepository.findAll()
                .stream()
                .map(TurnoResponseDTO::from)
                .toList();
    }

    @Override
    public List<TurnoResponseDTO> findTurnosDisponibles() {
        return turnoRepository.findTurnosDisponibles()
                .stream()
                .map(TurnoResponseDTO::from)
                .toList();
    }

    @Override
    public List<TurnoResponseDTO> findTurnosByServicioAndFecha(Long servicioId, LocalDate fecha) {
        return turnoRepository.findTurnosDisponiblesByServicioAndFecha(servicioId, fecha)
                .stream()
                .map(TurnoResponseDTO::from)
                .toList();
    }

    private Turno buscarTurnoPorId(Long id) {
        return turnoRepository.findById(id).
                orElseThrow(() -> new BusinessRuleViolationException(
                        "Turno no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND));
    }
}
