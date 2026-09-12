package com.devsenior.msal.reservation.controller;

import com.devsenior.msal.reservation.dto.request.CancelarReservaRequestDTO;
import com.devsenior.msal.reservation.dto.request.ReservaRequestDTO;
import com.devsenior.msal.reservation.dto.response.ReservaResponseDTO;
import com.devsenior.msal.reservation.enums.MotivoCancelacion;
import com.devsenior.msal.reservation.security.UserDetailsImpl;
import com.devsenior.msal.reservation.service.ReservaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reservas")
@Tag(name = "Gestión de Reservas", description = "Endpoints para administrar reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @PreAuthorize("hasAnyRole('CLIENTE','EMPLEADO')")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(
            @Valid @RequestBody ReservaRequestDTO reservaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.crearReserva(reservaRequest));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable Long id, @Valid @RequestBody(required = false) CancelarReservaRequestDTO request,
            Authentication authentication) {
        reservaService.cancelarReserva(id, request != null ? request.motivo() : null, authentication);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> findAllReservas() {
        return ResponseEntity.ok(reservaService.findAllReservas());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> findReservasByUsuarioId(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservaService.findReservasByUsuarioId(usuarioId));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>> findMisReservas(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(reservaService.findMisReservas(usuarioId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> findReservaById(
            @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.findReservaById(id));
    }
}
