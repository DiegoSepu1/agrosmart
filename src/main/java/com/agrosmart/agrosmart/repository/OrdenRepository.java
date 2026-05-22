package com.agrosmart.agrosmart.repository;

import com.agrosmart.agrosmart.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    /** Obtiene el historial de compras de un agricultor */
    List<Orden> findByIdAgricultorOrderByFechaCreacionDesc(Integer idAgricultor);

    /** Busca una orden por su token de Transbank */
    Optional<Orden> findByWebpayToken(String webpayToken);
}
