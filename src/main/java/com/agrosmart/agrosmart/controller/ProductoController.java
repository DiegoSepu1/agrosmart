package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.entity.Producto;
import com.agrosmart.agrosmart.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Método para listar todo el inventario
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }
}