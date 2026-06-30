package com.devsenior.msal.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
