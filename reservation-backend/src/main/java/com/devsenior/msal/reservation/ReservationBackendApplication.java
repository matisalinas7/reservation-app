package com.devsenior.msal.reservation;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
public class ReservationBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationBackendApplication.class, args);
        System.out.println("Reservation Backend Application started");
        org.flywaydb.core.Flyway prueba;
	}

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
