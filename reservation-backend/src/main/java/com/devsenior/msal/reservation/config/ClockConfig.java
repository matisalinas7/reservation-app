package com.devsenior.msal.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

// Reloj único para la lógica de negocio: la hora de los turnos es hora de Argentina,
// sin importar el huso horario de la máquina donde corra el backend.
@Configuration
public class ClockConfig {

    public static final ZoneId ZONA_ARGENTINA = ZoneId.of("America/Argentina/Buenos_Aires");

    @Bean
    public Clock clock() {
        return Clock.system(ZONA_ARGENTINA);
    }
}
