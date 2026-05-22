package com.agrosmart.agrosmart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrosmart.agrosmart.entity.Agricultor;

public interface AgricultorRepository extends JpaRepository<Agricultor, Integer> {

    /** Busca el agricultor a partir del id del usuario logueado */
    Optional<Agricultor> findByIdUsuario(Integer idUsuario);
}
