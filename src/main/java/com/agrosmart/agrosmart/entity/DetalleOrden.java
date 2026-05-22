package com.agrosmart.agrosmart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Mapea la tabla 'detalle_orden'.
 * Cada fila es un ítem del carrito dentro de una orden de compra.
 */
@Entity
@Table(name = "detalle_orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    /** FK a la orden padre */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    private Orden orden;

    /** FK al producto */
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_clp", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioClp;

    @Column(name = "precio_unitario_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioUsd;

    /** FK a la sucursal de origen del stock. Usamos ID 1 por defecto si no hay otra. */
    @Column(name = "id_sucursal_origen", nullable = false)
    private Integer idSucursalOrigen;

    public DetalleOrden() {}

    // ========== GETTERS Y SETTERS ==========
    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitarioClp() { return precioUnitarioClp; }
    public void setPrecioUnitarioClp(BigDecimal precioUnitarioClp) { this.precioUnitarioClp = precioUnitarioClp; }

    public BigDecimal getPrecioUnitarioUsd() { return precioUnitarioUsd; }
    public void setPrecioUnitarioUsd(BigDecimal precioUnitarioUsd) { this.precioUnitarioUsd = precioUnitarioUsd; }

    public Integer getIdSucursalOrigen() { return idSucursalOrigen; }
    public void setIdSucursalOrigen(Integer idSucursalOrigen) { this.idSucursalOrigen = idSucursalOrigen; }
}
