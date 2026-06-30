package com.devsenior.msal.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.msal.reservation.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
