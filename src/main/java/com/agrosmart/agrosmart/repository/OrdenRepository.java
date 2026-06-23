package com.agrosmart.agrosmart.repository;

import com.agrosmart.agrosmart.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    /** Obtiene el historial de compras de un agricultor */
    List<Orden> findByIdAgricultorOrderByFechaCreacionDesc(Integer idAgricultor);

    /** Busca una orden por su token de Transbank */
    Optional<Orden> findByWebpayToken(String webpayToken);

    /** Obtiene todas las órdenes ordenadas por fecha descendente */
    List<Orden> findAllByOrderByFechaCreacionDesc();

    /** Obtiene órdenes entre dos fechas */
    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY o.fechaCreacion DESC")
    List<Orden> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    /** Obtiene órdenes por estado */
    List<Orden> findByEstadoOrderByFechaCreacionDesc(String estado);
}
