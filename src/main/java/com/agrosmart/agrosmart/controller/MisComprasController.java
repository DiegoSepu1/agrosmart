package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.entity.Orden;
import com.agrosmart.agrosmart.repository.OrdenRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Historial de compras del cliente logueado (requisito 3.d / 5.1).
 *
 * Reusa el método ya existente del repositorio
 * {@code findByIdAgricultorOrderByFechaCreacionDesc}. Si el usuario no está
 * logueado como cliente, se le envía al login.
 */
@Controller
public class MisComprasController {

    @Autowired
    private OrdenRepository ordenRepository;

    @GetMapping("/mis-compras")
    public String misCompras(HttpSession session, Model model) {
        Integer idAgricultor = (Integer) session.getAttribute("idAgricultor");

        if (idAgricultor == null) {
            // No hay cliente logueado → enviar a login
            return "redirect:/login";
        }

        List<Orden> ordenes = ordenRepository.findByIdAgricultorOrderByFechaCreacionDesc(idAgricultor);
        model.addAttribute("ordenes", ordenes);
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        return "mis-compras";
    }
}
