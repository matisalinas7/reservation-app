package com.devsenior.msal.reservation.controller;

import com.devsenior.msal.reservation.dto.request.ActualizarRolRequestDTO;
import com.devsenior.msal.reservation.dto.request.UsuarioRequestDTO;
import com.devsenior.msal.reservation.dto.response.UsuarioResponseDTO;
import com.devsenior.msal.reservation.enums.Rol;
import com.devsenior.msal.reservation.service.UsuarioService;
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
@RequestMapping("/usuarios")
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administrar usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
            @Valid @RequestBody UsuarioRequestDTO usuarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarUsuario(usuarioRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable Long id){
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO usuarioRequest){
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarUsuario(
            @PathVariable Long id){
        usuarioService.reactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/rol")
    public ResponseEntity<Void> actualizarRol(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarRolRequestDTO request) {
        usuarioService.actualizarRol(id, request.rol());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findUsuarioById(
            @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findUsuarioById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> findAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAllUsuarios());
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> findAllUsuariosActivos() {
        return ResponseEntity.ok(usuarioService.findAllUsuariosActivos());
    }
}
