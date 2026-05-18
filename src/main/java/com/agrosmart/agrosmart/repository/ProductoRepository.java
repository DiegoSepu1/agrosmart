package com.agrosmart.agrosmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agrosmart.agrosmart.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}