package com.agrosmart.agrosmart.model;

import java.io.Serializable;

/**
 * Representa un ítem dentro del carrito de compras (NO es una entidad JPA).
 * Se serializa dentro de la HttpSession del servidor.
 */
public class CarritoItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idProducto;
    private String codigoProducto;
    private String nombre;
    private String urlImagen;
    private Double precioUnitarioClp;   // precio en CLP (con tipo de cambio aplicado)
    private Double precioUnitarioUsd;   // precio base en USD
    private Double tipoCambioUsado;
    private int cantidad;

    public CarritoItem() {}

    public CarritoItem(Integer idProducto, String codigoProducto, String nombre,
                    String urlImagen, Double precioUnitarioClp, Double precioUnitarioUsd,
                    Double tipoCambioUsado, int cantidad) {
        this.idProducto = idProducto;
        this.codigoProducto = codigoProducto;
        this.nombre = nombre;
        this.urlImagen = urlImagen;
        this.precioUnitarioClp = precioUnitarioClp;
        this.precioUnitarioUsd = precioUnitarioUsd;
        this.tipoCambioUsado = tipoCambioUsado;
        this.cantidad = cantidad;
    }

    /** Subtotal en CLP para este ítem */
    public double getSubtotalClp() {
        return (precioUnitarioClp != null ? precioUnitarioClp : 0.0) * cantidad;
    }

    /** Subtotal en USD para este ítem */
    public double getSubtotalUsd() {
        return (precioUnitarioUsd != null ? precioUnitarioUsd : 0.0) * cantidad;
    }

    // ========== GETTERS Y SETTERS ==========
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Double getPrecioUnitarioClp() { return precioUnitarioClp; }
    public void setPrecioUnitarioClp(Double precioUnitarioClp) { this.precioUnitarioClp = precioUnitarioClp; }

    public Double getPrecioUnitarioUsd() { return precioUnitarioUsd; }
    public void setPrecioUnitarioUsd(Double precioUnitarioUsd) { this.precioUnitarioUsd = precioUnitarioUsd; }

    public Double getTipoCambioUsado() { return tipoCambioUsado; }
    public void setTipoCambioUsado(Double tipoCambioUsado) { this.tipoCambioUsado = tipoCambioUsado; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
