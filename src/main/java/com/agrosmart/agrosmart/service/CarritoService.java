package com.agrosmart.agrosmart.service;

import com.agrosmart.agrosmart.entity.Producto;
import com.agrosmart.agrosmart.model.Carrito;
import com.agrosmart.agrosmart.model.CarritoItem;
import com.agrosmart.agrosmart.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio que gestiona el carrito de compras almacenado en la HttpSession.
 *
 * El carrito vive en: session.getAttribute("carrito")
 */
@Service
public class CarritoService {

    private static final String SESSION_KEY = "carrito";

    @Autowired
    private ProductoRepository productoRepository;

    // ============================================================
    // OBTENER / INICIALIZAR CARRITO
    // ============================================================

    /**
     * Obtiene el carrito de la sesión. Si no existe, crea uno vacío.
     */
    public Carrito obtenerCarrito(HttpSession session) {
        Carrito carrito = (Carrito) session.getAttribute(SESSION_KEY);
        if (carrito == null) {
            carrito = new Carrito();
            session.setAttribute(SESSION_KEY, carrito);
        }
        return carrito;
    }

    // ============================================================
    // AGREGAR PRODUCTO
    // ============================================================

    /**
     * Agrega un producto al carrito (o suma cantidad si ya existe).
     * Valida que el producto esté activo y tenga precio en CLP.
     *
     * @return mensaje de error (si hay), o null si todo fue bien
     */
    public String agregarProducto(HttpSession session, Integer idProducto, int cantidad) {
        Optional<Producto> opt = productoRepository.findById(idProducto);

        if (opt.isEmpty()) {
            return "Producto no encontrado.";
        }

        Producto producto = opt.get();

        if (Boolean.FALSE.equals(producto.getActivo())) {
            return "El producto no está disponible.";
        }

        if (producto.getPrecioClpCache() == null) {
            return "El precio de este producto aún no está sincronizado. Intenta más tarde.";
        }

        if (cantidad <= 0) {
            return "La cantidad debe ser mayor a cero.";
        }

        CarritoItem item = new CarritoItem(
                producto.getIdProducto(),
                producto.getCodigoProducto(),
                producto.getNombre(),
                producto.getUrlImagenProducto(),
                producto.getPrecioClpCache(),
                producto.getPrecioBaseUsd(),
                producto.getTipoCambioUsado(),
                cantidad
        );

        Carrito carrito = obtenerCarrito(session);
        carrito.agregarItem(item);
        session.setAttribute(SESSION_KEY, carrito);

        return null; // sin error
    }

    // ============================================================
    // ELIMINAR PRODUCTO
    // ============================================================

    public void eliminarProducto(HttpSession session, Integer idProducto) {
        Carrito carrito = obtenerCarrito(session);
        carrito.eliminarItem(idProducto);
        session.setAttribute(SESSION_KEY, carrito);
    }

    // ============================================================
    // ACTUALIZAR CANTIDAD
    // ============================================================

    public void actualizarCantidad(HttpSession session, Integer idProducto, int cantidad) {
        Carrito carrito = obtenerCarrito(session);
        carrito.actualizarCantidad(idProducto, cantidad);
        session.setAttribute(SESSION_KEY, carrito);
    }

    // ============================================================
    // VACIAR CARRITO
    // ============================================================

    public void vaciarCarrito(HttpSession session) {
        Carrito carrito = obtenerCarrito(session);
        carrito.vaciar();
        session.setAttribute(SESSION_KEY, carrito);
    }
}
