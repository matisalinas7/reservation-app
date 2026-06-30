package com.devsenior.msal.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
}
