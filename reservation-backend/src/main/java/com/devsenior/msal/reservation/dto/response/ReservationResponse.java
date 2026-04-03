package com.devsenior.msal.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.devsenior.msal.reservation.entity.Reservation;
import com.devsenior.msal.reservation.entity.ReservationStatus;

public record ReservationResponse(        
    Long id,
    String customerName,
    LocalDate date,
    LocalTime time,
    String service,
    ReservationStatus status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getCustomerName(),
            reservation.getDate(),
            reservation.getTime(),
            reservation.getService(),
            reservation.getStatus()
        );
    }
}
