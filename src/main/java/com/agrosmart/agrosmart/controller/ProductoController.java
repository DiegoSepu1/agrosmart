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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
    @GetMapping("/{idProducto}")
    public Producto buscarProductoPorId(@PathVariable Integer idProducto){
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado con ID " + idProducto));
    }

    @PostMapping
    @ResponseBody // <-- Obligatorio para que Postman reciba el JSON directamente
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {

        // 1. Validar si el JSON viene con un ID y si ese ID ya existe en la base de datos
        if (producto.getIdProducto() != null && productoRepository.existsById(producto.getIdProducto())) {
            throw new ResponseStatusException(
                    HttpStatus.FOUND,
                    "El producto ya existe con el ID " + producto.getIdProducto()
            );
        }

        // 2. Guardar el nuevo producto en la base de datos agrosmart_db
        Producto productoGuardado = productoRepository.save(producto);

        // 3. Responder a Postman con el objeto creado y un estado HTTP 200 OK
        return ResponseEntity.ok(productoGuardado);
    }
    @DeleteMapping("/{idProducto}")
    @ResponseBody // <-- Obligatorio para que Postman reciba la respuesta JSON sin buscar un HTML
    public ResponseEntity<Map<String, String>> eliminarProducto(@PathVariable Integer idProducto) {

        // 1. Validar si el producto existe antes de intentar borrarlo
        if (!productoRepository.existsById(idProducto)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Producto no encontrado con ID " + idProducto
            );
        }

        // 2. Eliminar el registro de la base de datos
        productoRepository.deleteById(idProducto);

        // 3. Construir la respuesta en formato JSON para Postman
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("Mensaje", "Producto eliminado");
        respuesta.put("Id producto", idProducto.toString());

        return ResponseEntity.ok(respuesta);
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
    @PutMapping("/{idProducto}")
    @ResponseBody // <-- Obligatorio para que Postman reciba el JSON de vuelta
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer idProducto, @RequestBody Producto productoActualizado) {

        // 1. Buscar si el producto existe en la base de datos antes de modificarlo
        Producto productoExistente = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado con ID " + idProducto));

        // 2. Actualizar los campos con la información que viene de Postman
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setPrecioBaseUsd(productoActualizado.getPrecioBaseUsd());
        productoExistente.setPrecioClpCache(productoActualizado.getPrecioClpCache());
        productoExistente.setStockFisico(productoActualizado.getStockFisico());
        productoExistente.setActivo(productoActualizado.getActivo());
        productoExistente.setCategoria(productoActualizado.getCategoria());

        // Si manejas imágenes o fichas técnicas, también puedes descomentar estas líneas:
        // productoExistente.setUrlImagenProducto(productoActualizado.getUrlImagenProducto());
        // productoExistente.setFichaTecnicaUrl(productoActualizado.getFichaTecnicaUrl());

        // 3. Guardar los cambios en la base de datos agrosmart_db
        Producto productoGuardado = productoRepository.save(productoExistente);

        // 4. Retornar el producto actualizado con un estado 200 OK
        return ResponseEntity.ok(productoGuardado);
    }
}