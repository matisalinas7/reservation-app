package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.HorarioRequestDTO;
import com.devsenior.msal.reservation.dto.response.HorarioResponseDTO;
import com.devsenior.msal.reservation.entity.Horario;
import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.entity.Servicio;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.HorarioRepository;
import com.devsenior.msal.reservation.repository.ReservaRepository;
import com.devsenior.msal.reservation.repository.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HorarioServiceImpl implements HorarioService {

    private HorarioRepository horarioRepository;
    private ServicioRepository servicioRepository;
    private ReservaRepository reservaRepository;

    public HorarioServiceImpl(HorarioRepository horarioRepository, ServicioRepository servicioRepository,  ReservaRepository reservaRepository) {
        this.horarioRepository = horarioRepository;
        this.servicioRepository = servicioRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public HorarioResponseDTO crearHorario(HorarioRequestDTO request) {
        Horario horario = new Horario();
        horario.setDiaSemana(request.diaSemana());
        horario.setHoraInicio(request.horaInicio());
        horario.setHoraFin(request.horaFin());

        Servicio servicio = servicioRepository.findById(request.servicioId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Servicio no encontrado con id: " + request.servicioId(),
                        HttpStatus.NOT_FOUND));
        horario.setServicio(servicio);

        return HorarioResponseDTO.from(horarioRepository.save(horario));
    }

    @Override
    public HorarioResponseDTO actualizarHorario(Long id, HorarioRequestDTO request) {
        Horario horarioExistente = buscarHorarioPorId(id);

        horarioExistente.setDiaSemana(request.diaSemana());
        horarioExistente.setHoraInicio(request.horaInicio());
        horarioExistente.setHoraFin(request.horaFin());

        Servicio servicio = servicioRepository.findById(request.servicioId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Servicio no encontrado con id: " + request.servicioId(),
                        HttpStatus.NOT_FOUND));
        horarioExistente.setServicio(servicio);

        return HorarioResponseDTO.from(horarioRepository.save(horarioExistente));
    }

    @Transactional
    @Override
    public void eliminarHorario(Long id) {
        Horario horarioElegido = buscarHorarioPorId(id);
        if(horarioElegido.getFechaBaja() != null) {
            throw new BusinessRuleViolationException(
                    "Este horario ya está dado de baja",
                    HttpStatus.CONFLICT);
        }
        horarioElegido.setFechaBaja(LocalDateTime.now());
        horarioRepository.save(horarioElegido);

        List<Reserva> reservasAfectadas = reservaRepository
                .findByTurno_HorarioAndEstadoAndTurno_FechaAfter(
                        horarioElegido,
                        ReservationStatus.ACTIVE,
                        LocalDate.now()
                );
        reservasAfectadas.forEach(reserva -> reserva.setEstado(ReservationStatus.CANCELLED));
        reservaRepository.saveAll(reservasAfectadas);
    }

    @Override
    public void reactivarHorario(Long id) {
        Horario horarioElegido = buscarHorarioPorId(id);
        if(horarioElegido.getFechaBaja() == null) {
            throw new BusinessRuleViolationException(
                    "Este horario ya está activo, no se puede reactivar",
                    HttpStatus.CONFLICT);
        }
        horarioElegido.setFechaBaja(null);
        horarioRepository.save(horarioElegido);
    }

    @Override
    public HorarioResponseDTO findHorarioById(Long id) {
        return HorarioResponseDTO.from(buscarHorarioPorId(id));
    }

    @Override
    public List<HorarioResponseDTO> findAllHorarios() {
        return horarioRepository.findAll()
                .stream()
                .map(HorarioResponseDTO::from)
                .toList();
    }

    @Override
    public List<HorarioResponseDTO> findHorariosDisponibles() {
        return horarioRepository.findByFechaBajaIsNull()
                .stream()
                .map(HorarioResponseDTO::from)
                .toList();
    }

    @Override
    public List<HorarioResponseDTO> findHorarioByServicioId(Long servicioId) {
        return horarioRepository.findByServicioId(servicioId)
                .stream()
                .map(HorarioResponseDTO::from)
                .toList();
    }

    private Horario buscarHorarioPorId(Long id){
        return horarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Horario no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND));
    }
}
