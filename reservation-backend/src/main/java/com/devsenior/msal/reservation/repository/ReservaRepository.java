package com.devsenior.msal.reservation.repository;

import com.devsenior.msal.reservation.entity.*;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByTurnoAndEstadoNot(Turno turno, ReservationStatus estado);

    List<Reserva> findByServicio_CategoriaAndEstadoAndTurno_FechaAfter(
            Categoria categoria,
            ReservationStatus estado,
            LocalDate fecha
    );

    List<Reserva> findByTurno_HorarioAndEstadoAndTurno_FechaAfter(
            Horario horario,
            ReservationStatus estado,
            LocalDate fecha
    );

    Optional<Reserva> findByTurnoAndEstado(Turno turno, ReservationStatus estado);

    List<Reserva> findByUsuarioAndEstadoAndTurno_FechaAfter(
            Usuario usuario,
            ReservationStatus estado,
            LocalDate fecha
    );

    List<Reserva> findByUsuario_Id(Long usuarioId);

    boolean existsByTurnoAndEstado(Turno turno, ReservationStatus reservationStatus);

    List<Reserva> findByEstadoAndTurno_FechaBefore(ReservationStatus estado, LocalDate fecha);
}
