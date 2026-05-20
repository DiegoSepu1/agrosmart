package com.agrosmart.agrosmart.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "codigo_producto", unique = true, nullable = false)
    private String codigoProducto;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private String categoria;

    @Column(name = "precio_base_usd", nullable = false)
    private Double precioBaseUsd;

    @Column(name = "precio_clp_cache")
    private Double precioClpCache;

    @Column(name = "tipo_cambio_usado")
    private Double tipoCambioUsado;

    @Column(name = "fecha_actualizacion_precio")
    private LocalDateTime fechaActualizacionPrecio;

    @Column(name = "ficha_tecnica_url")
    private String fichaTecnicaUrl;

    @Column(name = "peso_kg")
    private Double pesoKg;

    @Column(name = "requiere_aprobacion_credito")
    private Boolean requiereAprobacionCredito = false;

    private Boolean activo = true;

    @Column(name = "url_imagen_producto")
    private String urlImagenProducto;

    // 🌟 NUEVO CAMPO DE STOCK AÑADIDO AQUÍ
    @Column(name = "stock_fisico")
    private Integer stockFisico = 0;

    // Constructor vacío obligatorio en JPA
    public Producto() {}

    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getPrecioBaseUsd() { return precioBaseUsd; }
    public void setPrecioBaseUsd(Double precioBaseUsd) { this.precioBaseUsd = precioBaseUsd; }

    public Double getPrecioClpCache() { return precioClpCache; }
    public void setPrecioClpCache(Double precioClpCache) { this.precioClpCache = precioClpCache; }

    public Double getTipoCambioUsado() { return tipoCambioUsado; }
    public void setTipoCambioUsado(Double tipoCambioUsado) { this.tipoCambioUsado = tipoCambioUsado; }

    public LocalDateTime getFechaActualizacionPrecio() { return fechaActualizacionPrecio; }
    public void setFechaActualizacionPrecio(LocalDateTime fechaActualizacionPrecio) { this.fechaActualizacionPrecio = fechaActualizacionPrecio; }

    public String getFichaTecnicaUrl() { return fichaTecnicaUrl; }
    public void setFichaTecnicaUrl(String fichaTecnicaUrl) { this.fichaTecnicaUrl = fichaTecnicaUrl; }

    public Double getPesoKg() { return pesoKg; }
    public void setPesoKg(Double pesoKg) { this.pesoKg = pesoKg; }

    public Boolean getRequiereAprobacionCredito() { return requiereAprobacionCredito; }
    public void setRequiereAprobacionCredito(Boolean requiereAprobacionCredito) { this.requiereAprobacionCredito = requiereAprobacionCredito; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getUrlImagenProducto() { return urlImagenProducto; }
    public void setUrlImagenProducto(String urlImagenProducto) { this.urlImagenProducto = urlImagenProducto; }

    // 🌟 GETTER Y SETTER DEL STOCK AÑADIDOS AQUÍ
    public Integer getStockFisico() { return stockFisico; }
    public void setStockFisico(Integer stockFisico) { this.stockFisico = stockFisico; }
}