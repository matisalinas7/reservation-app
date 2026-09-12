package com.devsenior.msal.reservation.controller;

import com.devsenior.msal.reservation.dto.request.CategoriaRequestDTO;
import com.devsenior.msal.reservation.dto.response.CategoriaResponseDTO;
import com.devsenior.msal.reservation.entity.Categoria;

import com.devsenior.msal.reservation.service.CategoriaService;
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
@RequestMapping("/categorias")
@Tag(name = "Gestión de Categorías", description = "Endpoints para administrar categorías")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(
            @Valid @RequestBody CategoriaRequestDTO categoriaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.crearCategoria(categoriaRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> findAllCategorias() {
        return ResponseEntity.ok(categoriaService.findAllCategorias());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findCategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findCategoriaById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarCategoria(@PathVariable Long id) {
        categoriaService.reactivarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<CategoriaResponseDTO>> findAllCategoriasDisponibles() {
        return ResponseEntity.ok(categoriaService.findCategoriasDisponibles());
    }
}
