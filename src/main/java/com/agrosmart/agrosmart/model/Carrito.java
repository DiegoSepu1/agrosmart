package com.agrosmart.agrosmart.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Carrito de compras almacenado en la HttpSession del usuario.
 * NO es una entidad JPA. Se convierte a Orden/DetalleOrden al hacer checkout.
 *
 * Uso en sesión:
 *   session.getAttribute("carrito")  →  devuelve este objeto
 *   session.setAttribute("carrito", carrito)
 */
public class Carrito implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<CarritoItem> items = new ArrayList<>();

    // ============================================================
    // OPERACIONES DEL CARRITO
    // ============================================================

    /**
     * Agrega un producto. Si ya existe, suma la cantidad.
     */
    public void agregarItem(CarritoItem nuevoItem) {
        Optional<CarritoItem> existente = items.stream()
                .filter(i -> i.getIdProducto().equals(nuevoItem.getIdProducto()))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + nuevoItem.getCantidad());
        } else {
            items.add(nuevoItem);
        }
    }

    /**
     * Elimina un producto por su ID de producto.
     */
    public void eliminarItem(Integer idProducto) {
        items.removeIf(i -> i.getIdProducto().equals(idProducto));
    }

    /**
     * Actualiza la cantidad de un producto. Si cantidad <= 0, lo elimina.
     */
    public void actualizarCantidad(Integer idProducto, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            eliminarItem(idProducto);
            return;
        }
        items.stream()
                .filter(i -> i.getIdProducto().equals(idProducto))
                .findFirst()
                .ifPresent(i -> i.setCantidad(nuevaCantidad));
    }

    /**
     * Vacía el carrito completamente (se llama tras confirmar el pago).
     */
    public void vaciar() {
        items.clear();
    }

    // ============================================================
    // TOTALES Y RESUMEN
    // ============================================================

    /** Número total de ítems (suma de cantidades, no número de líneas) */
    public int getTotalItems() {
        return items.stream().mapToInt(CarritoItem::getCantidad).sum();
    }

    /** Total en CLP (usado para Transbank — debe ser entero) */
    public long getTotalClp() {
        return Math.round(items.stream().mapToDouble(CarritoItem::getSubtotalClp).sum());
    }

    /** Total en USD base */
    public double getTotalUsd() {
        return items.stream().mapToDouble(CarritoItem::getSubtotalUsd).sum();
    }

    /** True si el carrito no tiene ítems */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CarritoItem> getItems() {
        return items;
    }
}
