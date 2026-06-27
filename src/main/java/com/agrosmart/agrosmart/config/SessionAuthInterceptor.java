package com.agrosmart.agrosmart.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Control de acceso basado en la HttpSession (sin Spring Security).
 *
 * Protege únicamente las rutas de administración. El resto del sitio
 * (catálogo, carrito, checkout, pago y retorno de Transbank) permanece público,
 * tal como funcionaba antes, para no romper el flujo de compra del cliente.
 *
 * Un usuario es "administrador" si su atributo de sesión 'rolUsuario' lo es
 * (ver {@link Roles}). Si no lo es:
 *   - Peticiones a /api/**  → 403 Forbidden (JSON/AJAX).
 *   - Peticiones de páginas → redirect a /login.
 */
public class SessionAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        String rol = (session != null) ? (String) session.getAttribute("rolUsuario") : null;

        if (Roles.esAdmin(rol)) {
            return true; // administrador autenticado → acceso permitido
        }

        // No autorizado
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Acceso restringido: se requiere perfil administrador.");
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        return false;
    }
}
