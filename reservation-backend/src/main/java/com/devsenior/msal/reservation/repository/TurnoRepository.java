package com.devsenior.msal.reservation.repository;

import com.devsenior.msal.reservation.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Turno;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByFechaBajaIsNull();

    @Query("SELECT t FROM Turno t WHERE t.fechaBaja IS NULL AND t NOT IN (SELECT r.turno FROM Reserva r WHERE r.estado = 'ACTIVE')")
    List<Turno> findTurnosDisponibles();

    @Query("SELECT t FROM Turno t WHERE t.horario.servicio.id = :servicioId AND t.fecha = :fecha AND t.fechaBaja IS NULL AND t NOT IN (SELECT r.turno FROM Reserva r WHERE r.estado = 'ACTIVE')")
    List<Turno> findTurnosDisponiblesByServicioAndFecha(
            @Param("servicioId") Long servicioId,
            @Param("fecha") LocalDate fecha
    );}
