package com.devsenior.msal.reservation.controller;

import com.devsenior.msal.reservation.dto.request.ServicioRequestDTO;
import com.devsenior.msal.reservation.dto.response.ServicioResponseDTO;
import com.devsenior.msal.reservation.service.ServicioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/servicios")
@Tag(name = "Gestión de Servicios", description = "Endpoints para administrar servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crearServicio(
            @Valid @RequestBody ServicioRequestDTO servicioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicioService.crearServicio(servicioRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(
            @PathVariable Long id) {
        servicioService.eliminarServicio(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizarServicio(
            @PathVariable Long id, @Valid @RequestBody ServicioRequestDTO servicioRequest) {
        return ResponseEntity.ok(servicioService.actualizarServicio(id, servicioRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> findAllServicios() {
        return ResponseEntity.ok(servicioService.findAllServicios());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> findServicioById(
            @PathVariable Long id) {
        return ResponseEntity.ok(servicioService.findServicioById(id));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ServicioResponseDTO>> findServiciosDisponibles() {
        return ResponseEntity.ok(servicioService.findServiciosDisponibles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarServicio(
            @PathVariable Long id) {
        servicioService.reactivarServicio(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ServicioResponseDTO>> findServicioByCategoriaId(
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(servicioService.findServicioByCategoriaId(categoriaId));
    }
}
