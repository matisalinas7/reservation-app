package com.devsenior.msal.reservation.service;

import com.devsenior.msal.reservation.dto.request.ServicioRequestDTO;
import com.devsenior.msal.reservation.dto.response.ServicioResponseDTO;
import com.devsenior.msal.reservation.entity.Categoria;
import com.devsenior.msal.reservation.entity.Servicio;
import com.devsenior.msal.reservation.exception.BusinessRuleViolationException;
import com.devsenior.msal.reservation.repository.CategoriaRepository;
import com.devsenior.msal.reservation.repository.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;

    public ServicioServiceImpl(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository) {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public ServicioResponseDTO crearServicio(ServicioRequestDTO request) {
        Servicio servicio = new Servicio();
        servicio.setNombre(request.nombre());
        servicio.setDescripcion(request.descripcion());
        servicio.setDuracion(request.duracion());

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Categoría no encontrada con id: " + request.categoriaId(),
                        HttpStatus.NOT_FOUND));
        servicio.setCategoria(categoria);

        return ServicioResponseDTO.from(servicioRepository.save(servicio));
    }

    @Transactional
    @Override
    public void eliminarServicio(Long id) {
        Servicio servicioElegido = buscarServicioPorId(id);
        if(servicioElegido.getFechaBaja() != null) {
         throw new BusinessRuleViolationException(
                 "Este servicio ya está dado de baja",
                 HttpStatus.CONFLICT);
        }
        servicioElegido.setFechaBaja(LocalDateTime.now());
        servicioRepository.save(servicioElegido);
    }

    @Override
    public ServicioResponseDTO actualizarServicio(Long id, ServicioRequestDTO request) {
        Servicio servicioExistente = buscarServicioPorId(id);

        servicioExistente.setNombre(request.nombre());
        servicioExistente.setDescripcion(request.descripcion());
        servicioExistente.setDuracion(request.duracion());

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Categoría no encontrada con id: " + request.categoriaId(),
                        HttpStatus.NOT_FOUND));
        servicioExistente.setCategoria(categoria);

        return ServicioResponseDTO.from(servicioRepository.save(servicioExistente));
    }

    @Override
    public void reactivarServicio(Long id) {
        Servicio servicioElegido = buscarServicioPorId(id);
        if(servicioElegido.getFechaBaja() == null) {
            throw new BusinessRuleViolationException(
                    "Este servicio ya está activo, no se puede reactivar",
                    HttpStatus.CONFLICT);
        }
        servicioElegido.setFechaBaja(null);
        servicioRepository.save(servicioElegido);
    }

    @Override
    public ServicioResponseDTO findServicioById(Long id) {
        return ServicioResponseDTO.from(buscarServicioPorId(id));
    }

    @Override
    public List<ServicioResponseDTO> findAllServicios() {
        return servicioRepository.findAll()
                .stream()
                .map(ServicioResponseDTO::from)
                .toList();
    }

    @Override
    public List<ServicioResponseDTO> findServiciosDisponibles() {
        return servicioRepository.findByFechaBajaIsNull()
                .stream()
                .map(ServicioResponseDTO::from)
                .toList();
    }

    @Override
    public List<ServicioResponseDTO> findServicioByCategoriaId(Long categoriaId) {
        return servicioRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(ServicioResponseDTO::from)
                .toList();
    }

    private Servicio buscarServicioPorId(Long id){
        return servicioRepository.findById(id).
                orElseThrow(() -> new BusinessRuleViolationException(
                        "Servicio no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND));
    }
}
