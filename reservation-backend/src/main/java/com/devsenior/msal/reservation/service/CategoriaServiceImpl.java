package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.CategoriaRequestDTO;
import com.devsenior.msal.reservation.dto.response.CategoriaResponseDTO;
import com.devsenior.msal.reservation.entity.Categoria;
import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.enums.ReservationStatus;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.CategoriaRepository;
import com.devsenior.msal.reservation.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ReservaRepository reservaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository,
                                ReservaRepository reservaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        return CategoriaResponseDTO.from(categoriaGuardada);
    }

    @Override
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO request) {
        Categoria categoriaExistente = buscarCategoriaPorId(id);

        categoriaExistente.setNombre(request.nombre());
        categoriaExistente.setDescripcion(request.descripcion());

        Categoria categoriaActualizada = categoriaRepository.save(categoriaExistente);

        return CategoriaResponseDTO.from(categoriaActualizada);
    }

    @Transactional
    @Override
    public void eliminarCategoria(Long id) {
        Categoria categoriaElegida = buscarCategoriaPorId(id);
        if (categoriaElegida.getFechaBaja() != null) {
            throw new BusinessRuleViolationException(
                    "Esta categoría ya está dada de baja",
                    HttpStatus.CONFLICT);
        }
        categoriaElegida.setFechaBaja(LocalDateTime.now());
        categoriaRepository.save(categoriaElegida);

        List<Reserva> reservasAfectadas = reservaRepository
                .findByServicio_CategoriaAndEstadoAndTurno_FechaAfter(
                        categoriaElegida,
                        ReservationStatus.ACTIVE,
                        LocalDate.now()
                );
        reservasAfectadas.forEach(reserva -> reserva.setEstado(ReservationStatus.CANCELLED));
        reservaRepository.saveAll(reservasAfectadas);
    }

    @Override
    public void reactivarCategoria(Long id) {
        Categoria categoriaElegida = buscarCategoriaPorId(id);
        if (categoriaElegida.getFechaBaja() == null) {
            throw new BusinessRuleViolationException(
                    "Esta categoría está activa, no se puede reactivar.",
                    HttpStatus.CONFLICT);
        }
        categoriaElegida.setFechaBaja(null);
        categoriaRepository.save(categoriaElegida);
    }

    @Override
    public CategoriaResponseDTO findCategoriaById(Long id) {
        return CategoriaResponseDTO.from(buscarCategoriaPorId(id));
    }

    @Override
    public List<CategoriaResponseDTO> findAllCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaResponseDTO::from)
                .toList();

    }

    @Override
    public List<CategoriaResponseDTO> findCategoriasDisponibles() {
        return categoriaRepository.findByFechaBajaIsNull()
                .stream()
                .map(CategoriaResponseDTO::from)
                .toList();
    }

    private Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Categoría no encontrada con id: " + id,
                        HttpStatus.NOT_FOUND));
    }
}
