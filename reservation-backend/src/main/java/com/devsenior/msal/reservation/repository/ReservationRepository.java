package com.devsenior.msal.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Reservation;
import com.devsenior.msal.reservation.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByDateAndTimeAndStatusNot(LocalDate date, LocalTime time, ReservationStatus status);
}