package com.agrosmart.agrosmart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Mapea la tabla 'agricultor'.
 * Nota: id_usuario se guarda como Integer simple para evitar conflictos
 * con la entidad Usuario que ya maneja tu sistema de login.
 */
@Entity
@Table(name = "agricultor")
public class Agricultor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agricultor")
    private Integer idAgricultor;

    /** FK al usuario del sistema de login. Se guarda como Integer para compatibilidad. */
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "razon_social", length = 150)
    private String razonSocial;

    @Column(name = "direccion_predio", columnDefinition = "TEXT")
    private String direccionPredio;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitud;

    @Column(name = "tiene_linea_credito")
    private Boolean tieneLineaCredito = false;

    @Column(name = "monto_credito_maximo", precision = 12, scale = 2)
    private BigDecimal montoCreditoMaximo = BigDecimal.ZERO;

    @Column(name = "saldo_credito_disponible", precision = 12, scale = 2)
    private BigDecimal saldoCreditoDisponible = BigDecimal.ZERO;

    public Agricultor() {}

    public Integer getIdAgricultor() { return idAgricultor; }
    public void setIdAgricultor(Integer idAgricultor) { this.idAgricultor = idAgricultor; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getDireccionPredio() { return direccionPredio; }
    public void setDireccionPredio(String direccionPredio) { this.direccionPredio = direccionPredio; }

    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }

    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }

    public Boolean getTieneLineaCredito() { return tieneLineaCredito; }
    public void setTieneLineaCredito(Boolean tieneLineaCredito) { this.tieneLineaCredito = tieneLineaCredito; }

    public BigDecimal getMontoCreditoMaximo() { return montoCreditoMaximo; }
    public void setMontoCreditoMaximo(BigDecimal montoCreditoMaximo) { this.montoCreditoMaximo = montoCreditoMaximo; }

    public BigDecimal getSaldoCreditoDisponible() { return saldoCreditoDisponible; }
    public void setSaldoCreditoDisponible(BigDecimal saldoCreditoDisponible) { this.saldoCreditoDisponible = saldoCreditoDisponible; }
}
