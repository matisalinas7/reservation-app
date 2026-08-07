package com.devsenior.msal.reservation.controller;

import com.devsenior.msal.reservation.dto.request.TurnoRequestDTO;
import com.devsenior.msal.reservation.dto.response.TurnoResponseDTO;
import com.devsenior.msal.reservation.service.TurnoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/turnos")
@Tag(name = "Gestión de Turnos", description = "Endpoints para administrar turnos")
public class TurnoController {

    private final TurnoService turnoService;

    @PostMapping
    public ResponseEntity<List<TurnoResponseDTO>> generarTurno(
            @Valid @RequestBody TurnoRequestDTO turnoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(turnoService.generarTurno(turnoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(
            @PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarTurno(
            @PathVariable Long id) {
        turnoService.reactivarTurno(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> findTurnoById(
            @PathVariable Long id) {
        return ResponseEntity.ok(turnoService.findTurnoById(id));
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> findAllTurnos() {
        return ResponseEntity.ok(turnoService.findAllTurnos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<TurnoResponseDTO>> findTurnosDisponibles() {
        return ResponseEntity.ok(turnoService.findTurnosDisponibles());
    }

    @GetMapping("/servicio/{servicioId}")
    public ResponseEntity<List<TurnoResponseDTO>> findTurnosByServicioAndFecha(
            @PathVariable Long servicioId, @RequestParam LocalDate fecha) {
        return ResponseEntity.ok(turnoService.findTurnosByServicioAndFecha(servicioId, fecha));
    }

}
