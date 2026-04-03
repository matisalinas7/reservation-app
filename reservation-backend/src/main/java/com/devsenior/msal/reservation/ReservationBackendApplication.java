package com.devsenior.msal.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReservationBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationBackendApplication.class, args);
		System.out.println("Reservation Backend Application started");
	}

}
