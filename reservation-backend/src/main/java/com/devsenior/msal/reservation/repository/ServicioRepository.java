package com.devsenior.msal.reservation.repository;

import com.devsenior.msal.reservation.dto.response.ServicioResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Servicio;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByFechaBajaIsNull();
    List<Servicio> findByCategoriaId(Long categoriaId);
}
