package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.entity.Producto;
import com.agrosmart.agrosmart.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
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


    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Integer id) { // <-- Aquí cambiamos Long por Integer
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return "Éxito: Producto con ID " + id + " eliminado correctamente de la base de datos.";
        } else {
            return "Error: No se encontró ningún producto con el ID " + id + ".";
        }
    }
    @PostMapping("/lote")
    public List<Producto> guardarMultiplesProductos(@RequestBody List<Producto> productos) {
        // saveAll() toma la lista completa y la guarda de una pasada en MySQL
        return productoRepository.saveAll(productos);
    }
    // Método para actualizar el stock desde la vista del Jefe de Almacén
    @PatchMapping("/{id}/stock")
    public String actualizarStock(@PathVariable Integer id, @RequestParam Integer cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setStockFisico(cantidad);
            productoRepository.save(producto);
            return "Éxito: Stock actualizado a " + cantidad;
        } else {
            return "Error: No se encontró el producto.";
        }
    }
}