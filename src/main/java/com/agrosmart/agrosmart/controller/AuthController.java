package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.config.Roles;
import com.agrosmart.agrosmart.entity.Usuario;
import com.agrosmart.agrosmart.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // LOGIN
    // ============================================================

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("idUsuario") != null) {
            return destinoSegunRol((String) session.getAttribute("rolUsuario"));
        }
        return "login";
    }

    /** El admin va a su panel; el cliente continúa al checkout. */
    private String destinoSegunRol(String rol) {
        return Roles.esAdmin(rol) ? "redirect:/inventario/admin" : "redirect:/checkout";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.login(email, password, session);

        if (usuario != null) {
            return destinoSegunRol(usuario.getRol());
        } else {
            redirectAttributes.addFlashAttribute("errorLogin", "Credenciales inválidas.");
            return "redirect:/login";
        }
    }

    // ============================================================
    // REGISTRO
    // ============================================================

    @GetMapping("/registro")
    public String mostrarRegistro(HttpSession session) {
        if (session.getAttribute("idUsuario") != null) {
            return "redirect:/checkout";
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String telefono,
                                  @RequestParam String rut,
                                  @RequestParam String direccion,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            usuarioService.registrarAgricultor(nombre, email, password, telefono, rut, direccion);
            // Autologin después de registrar
            usuarioService.login(email, password, session);
            return "redirect:/checkout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorRegistro", e.getMessage());
            return "redirect:/registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        usuarioService.logout(session);
        return "redirect:/inventario";
    }
}
