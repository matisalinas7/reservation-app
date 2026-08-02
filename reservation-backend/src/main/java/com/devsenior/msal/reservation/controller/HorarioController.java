package com.devsenior.msal.reservation.controller;

import com.devsenior.msal.reservation.dto.request.HorarioRequestDTO;
import com.devsenior.msal.reservation.dto.response.HorarioResponseDTO;
import com.devsenior.msal.reservation.service.HorarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/horarios")
@Tag(name = "Gestión de horarios", description = "Endpoints para administrar horarios")
public class HorarioController {

    private final HorarioService horarioService;

    @PostMapping
    public ResponseEntity<HorarioResponseDTO> crearHorario(
            @Valid @RequestBody HorarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(horarioService.crearHorario(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(
            @PathVariable Long id) {
        horarioService.eliminarHorario(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> actualizarHorario(
            @PathVariable Long id, @Valid @RequestBody HorarioRequestDTO request) {
        return ResponseEntity.ok(horarioService.actualizarHorario(id, request));
    }

    @GetMapping
    public ResponseEntity<List<HorarioResponseDTO>> findAllHorarios(){
        return ResponseEntity.ok(horarioService.findAllHorarios());
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarHorario(
            @PathVariable Long id){
        horarioService.reactivarHorario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> findHorarioById(
            @PathVariable Long id){
        return ResponseEntity.ok(horarioService.findHorarioById(id));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<HorarioResponseDTO>> findHorariosDisponibles(){
        return ResponseEntity.ok(horarioService.findHorariosDisponibles());
    }

    @GetMapping("/servicio/{servicioId}")
    public ResponseEntity<List<HorarioResponseDTO>> findHorarioByServicioId(
            @PathVariable Long servicioId){
        return ResponseEntity.ok(horarioService.findHorarioByServicioId(servicioId));
    }
}
