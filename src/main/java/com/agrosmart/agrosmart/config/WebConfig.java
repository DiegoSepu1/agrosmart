package com.agrosmart.agrosmart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra el {@link SessionAuthInterceptor} SOLO sobre las rutas de administración.
 *
 * Rutas protegidas (requieren sesión con rol administrador):
 *   - /admin/**                → panel de clientes, ventas y pagos
 *   - /inventario/admin        → mantenedor de productos
 *   - /inventario/informes     → informes de ventas/stock
 *   - /api/inventario/**       → API CRUD de productos (POST/PUT/DELETE/PATCH)
 *
 * Rutas que permanecen PÚBLICAS (no se incluyen):
 *   - /                        (home)
 *   - /inventario              (catálogo)
 *   - /carrito/**              (carrito)
 *   - /checkout/**             (checkout)
 *   - /transbank/**            (retorno de pago)
 *   - /login, /registro, /logout
 *   - /mis-compras             (requiere su propia validación de cliente)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionAuthInterceptor())
                .addPathPatterns(
                        "/admin/**",
                        "/inventario/admin",
                        "/inventario/informes",
                        "/api/inventario/**"
                );
    }
}
