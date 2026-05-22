package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.model.Carrito;
import com.agrosmart.agrosmart.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador del carrito de compras.
 *
 * Rutas:
 *   GET  /carrito              → ver el carrito
 *   POST /carrito/agregar      → agregar un producto
 *   POST /carrito/eliminar     → eliminar un producto
 *   POST /carrito/actualizar   → cambiar la cantidad de un producto
 *   POST /carrito/vaciar       → vaciar todo el carrito
 */
@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // ============================================================
    // VER CARRITO
    // ============================================================

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Carrito carrito = carritoService.obtenerCarrito(session);
        model.addAttribute("carrito", carrito);
        return "carrito";
    }

    // ============================================================
    // AGREGAR PRODUCTO
    // ============================================================

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Integer idProducto,
                                   @RequestParam(defaultValue = "1") int cantidad,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        String error = carritoService.agregarProducto(session, idProducto, cantidad);

        if (error != null) {
            redirectAttributes.addFlashAttribute("errorCarrito", error);
            return "redirect:/inventario";
        }

        redirectAttributes.addFlashAttribute("exitoCarrito", "Producto añadido al carrito.");
        return "redirect:/inventario";
    }

    // ============================================================
    // ELIMINAR PRODUCTO
    // ============================================================

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Integer idProducto,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        carritoService.eliminarProducto(session, idProducto);
        redirectAttributes.addFlashAttribute("exitoCarrito", "Producto eliminado del carrito.");
        return "redirect:/carrito";
    }

    // ============================================================
    // ACTUALIZAR CANTIDAD
    // ============================================================

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer idProducto,
                                      @RequestParam int cantidad,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        carritoService.actualizarCantidad(session, idProducto, cantidad);

        if (cantidad <= 0) {
            redirectAttributes.addFlashAttribute("exitoCarrito", "Producto eliminado del carrito.");
        } else {
            redirectAttributes.addFlashAttribute("exitoCarrito", "Cantidad actualizada.");
        }

        return "redirect:/carrito";
    }

    // ============================================================
    // VACIAR CARRITO
    // ============================================================

    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session, RedirectAttributes redirectAttributes) {
        carritoService.vaciarCarrito(session);
        redirectAttributes.addFlashAttribute("exitoCarrito", "Carrito vaciado.");
        return "redirect:/carrito";
    }
}
