package com.devsenior.msal.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class ReservationBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationBackendApplication.class, args);
		System.out.println("Reservation Backend Application started");
	}

}
