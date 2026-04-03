package com.devsenior.msal.reservation.service;

import java.util.List;

import com.devsenior.msal.reservation.dto.request.CreateReservationRequest;
import com.devsenior.msal.reservation.entity.Reservation;

public interface ReservationService {
    List<Reservation> getAll();
    Reservation createReservation(CreateReservationRequest request);
    void cancelReservation(Long id);
}
