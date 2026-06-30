package com.devsenior.msal.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Turno;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
}
