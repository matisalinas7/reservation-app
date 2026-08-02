package com.devsenior.msal.reservation.repository;

import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Horario;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByFechaBajaIsNull();
    List<Horario> findByServicioId(Long servicioId);
}
