package com.devsenior.msal.reservation.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.msal.reservation.dto.request.CreateReservationRequest;
import com.devsenior.msal.reservation.entity.Reservation;
import com.devsenior.msal.reservation.entity.ReservationStatus;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation createReservation(CreateReservationRequest request) {
        if (reservationRepository.existsByDateAndTimeAndStatusNot(
                request.date(), request.time(), ReservationStatus.CANCELLED)) {
            throw new BusinessRuleViolationException(
                    "There is already an active reservation for this date and time.",
                    HttpStatus.CONFLICT);
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerName(request.customerName());
        reservation.setDate(request.date());
        reservation.setTime(request.time());
        reservation.setService(request.service());
        reservation.setStatus(ReservationStatus.ACTIVE);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository
                .findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Reservation not found with id: " + id, HttpStatus.NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleViolationException(
                    "Reservation is already cancelled.", HttpStatus.CONFLICT);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }
}
