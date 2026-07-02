package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.entity.Categoria;
import com.devsenior.msal.reservation.entity.Reserva;
import com.devsenior.msal.reservation.entity.Servicio;
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
    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria actualizarCategoria(Long id, Categoria categoria) {
        Categoria categoriaExistente = findCategoriaById(id);
        categoriaExistente.setNombre(categoria.getNombre());
        categoriaExistente.setDescripcion(categoria.getDescripcion());
        return categoriaRepository.save(categoriaExistente);
    }

    @Transactional
    @Override
    public void eliminarCategoria(Long id) {
        Categoria categoriaElegida = findCategoriaById(id);
        if (categoriaElegida.getFechaBaja() != null) {
            throw new BusinessRuleViolationException(
                    "Esta categoría ya está dada de baja",
                    HttpStatus.CONFLICT);
        } else {
            categoriaElegida.setFechaBaja(LocalDateTime.now());
            categoriaRepository.save(categoriaElegida);
        }

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
        Categoria categoriaElegida = findCategoriaById(id);
        if (categoriaElegida.getFechaBaja() == null) {
            throw new BusinessRuleViolationException(
                    "Esta categoría está activa, no se puede reactivar.",
                    HttpStatus.CONFLICT);
        }
        categoriaElegida.setFechaBaja(null);
        categoriaRepository.save(categoriaElegida);
    }

    @Override
    public Categoria findCategoriaById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Categoría no encontrada con id: " + id,
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Categoria> findAllCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public List<Categoria> findCategoriasDisponibles() {
        return categoriaRepository.findByFechaBajaIsNull();
    }
}
