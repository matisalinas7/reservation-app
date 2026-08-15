package com.devsenior.msal.reservation.controller;

import com.devsenior.msal.reservation.dto.request.CancelarReservaRequestDTO;
import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.enums.MotivoCancelacion;
import com.devsenior.msal.reservation.service.ReservaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reservas")
@Tag(name = "Gestión de Reservas", description = "Endpoints para administrar reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(
            @Valid @RequestBody ReservaRequestDTO reservaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.crearReserva(reservaRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable Long id, @Valid @RequestBody(required = false) CancelarReservaRequestDTO request) {
        reservaService.cancelarReserva(id, request != null ? request.motivo() : null);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> findAllReservas() {
        return ResponseEntity.ok(reservaService.findAllReservas());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> findReservasByUsuarioId(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservaService.findReservasByUsuarioId(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> findReservaById(
            @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.findReservaById(id));
    }
}
