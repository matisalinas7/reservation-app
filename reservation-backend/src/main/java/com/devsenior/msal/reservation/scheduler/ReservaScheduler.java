package com.devsenior.msal.reservation.scheduler;

import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservaScheduler {

    private final ReservaRepository reservaRepository;
    private final Clock clock;

    @Scheduled(cron = "0 0 0 * * *")
    public void completarReservasPasadas() {
        List<Reserva> reservasPasadas = reservaRepository
                .findByEstadoAndTurno_FechaBefore(
                        ReservationStatus.ACTIVE,
                        LocalDate.now(clock)
                );
        reservasPasadas.forEach(r -> r.setEstado(ReservationStatus.COMPLETED));
        reservaRepository.saveAll(reservasPasadas);
    }
}
