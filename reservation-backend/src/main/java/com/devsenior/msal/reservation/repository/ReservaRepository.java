package com.devsenior.msal.reservation.repository;

import com.devsenior.msal.reservation.entity.Categoria;
import com.devsenior.msal.reservation.entity.Turno;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Reserva;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByTurnoAndEstadoNot(Turno turno, ReservationStatus estado);

    List<Reserva> findByServicio_CategoriaAndEstadoAndTurno_FechaAfter(
            Categoria categoria,
            ReservationStatus estado,
            LocalDate fecha
    );
}
