package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inventario")
public class InventarioViewController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public String verInventario(Model model) {
        // Buscamos los productos en MySQL y los pasamos a la vista HTML
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario"; // Esto busca el archivo 'inventario.html' en la carpeta templates
    }
    @GetMapping("/admin")
    public String verPanelAdmin(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin"; // Esto buscará el archivo 'admin.html' en templates
    }
}