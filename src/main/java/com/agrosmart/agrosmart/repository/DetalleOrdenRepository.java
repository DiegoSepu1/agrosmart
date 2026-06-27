package com.agrosmart.agrosmart.repository;

import com.agrosmart.agrosmart.entity.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Integer> {

    /** Detalles (líneas) de una orden específica. */
    List<DetalleOrden> findByOrden_IdOrden(Integer idOrden);
}
