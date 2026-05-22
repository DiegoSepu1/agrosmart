package com.agrosmart.agrosmart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapea la tabla 'orden'. Soporta el flujo completo de compra,
 * incluyendo los campos de Transbank Webpay Plus.
 */
@Entity
@Table(name = "orden")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer idOrden;

    /** FK al agricultor comprador (nullable para compras como invitado) */
    @Column(name = "id_agricultor", nullable = true)
    private Integer idAgricultor;

    // ========== DATOS DEL CLIENTE (PARA INVITADOS O RESPALDO) ==========
    @Column(name = "cliente_nombre", length = 150)
    private String clienteNombre;

    @Column(name = "cliente_email", length = 100)
    private String clienteEmail;

    @Column(name = "cliente_telefono", length = 20)
    private String clienteTelefono;

    @Column(name = "cliente_rut", length = 12)
    private String clienteRut;

    /** FK al asesor técnico asignado (puede ser null al crear) */
    @Column(name = "id_asesor_asignado")
    private Integer idAsesorAsignado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /**
     * Estados posibles (ENUM en BD):
     * cotizacion | pendiente_credito | aprobada | pago_pendiente |
     * pago_confirmado | preparacion | despachada | entregada | cancelada
     */
    @Column(nullable = false, length = 50)
    private String estado = "cotizacion";

    /**
     * Tipo de entrega (ENUM en BD):
     * retiro_programado | despacho_predio
     */
    @Column(name = "tipo_entrega", nullable = false, length = 30)
    private String tipoEntrega;

    /** FK a la sucursal de retiro (nullable si es despacho a predio) */
    @Column(name = "id_sucursal_retiro")
    private Integer idSucursalRetiro;

    @Column(name = "direccion_despacho", columnDefinition = "TEXT")
    private String direccionDespacho;

    @Column(name = "fecha_programada_retiro")
    private LocalDateTime fechaProgramadaRetiro;

    /**
     * Método de pago (ENUM en BD):
     * transferencia_webpay | credito_agricola | factura_30dias
     */
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago;

    @Column(name = "total_clp", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalClp;

    @Column(name = "total_usd_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalUsdBase;

    @Column(name = "tipo_cambio_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal tipoCambioAplicado;

    // ========== CAMPOS TRANSBANK ==========
    @Column(name = "webpay_token", length = 255)
    private String webpayToken;

    /**
     * Estado Webpay (ENUM en BD): inicial | aprobado | rechazado
     */
    @Column(name = "webpay_status", length = 20)
    private String webpayStatus = "inicial";

    @Column(name = "webpay_callback_recibido")
    private Boolean webpayCallbackRecibido = false;

    // ========== RELACIÓN CON DETALLES ==========
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleOrden> detalles = new ArrayList<>();

    // ========== CICLO DE VIDA ==========
    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Orden() {}

    // ========== GETTERS Y SETTERS ==========
    public Integer getIdOrden() { return idOrden; }
    public void setIdOrden(Integer idOrden) { this.idOrden = idOrden; }

    public Integer getIdAgricultor() { return idAgricultor; }
    public void setIdAgricultor(Integer idAgricultor) { this.idAgricultor = idAgricultor; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }

    public String getClienteRut() { return clienteRut; }
    public void setClienteRut(String clienteRut) { this.clienteRut = clienteRut; }

    public Integer getIdAsesorAsignado() { return idAsesorAsignado; }
    public void setIdAsesorAsignado(Integer idAsesorAsignado) { this.idAsesorAsignado = idAsesorAsignado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public Integer getIdSucursalRetiro() { return idSucursalRetiro; }
    public void setIdSucursalRetiro(Integer idSucursalRetiro) { this.idSucursalRetiro = idSucursalRetiro; }

    public String getDireccionDespacho() { return direccionDespacho; }
    public void setDireccionDespacho(String direccionDespacho) { this.direccionDespacho = direccionDespacho; }

    public LocalDateTime getFechaProgramadaRetiro() { return fechaProgramadaRetiro; }
    public void setFechaProgramadaRetiro(LocalDateTime fechaProgramadaRetiro) { this.fechaProgramadaRetiro = fechaProgramadaRetiro; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getTotalClp() { return totalClp; }
    public void setTotalClp(BigDecimal totalClp) { this.totalClp = totalClp; }

    public BigDecimal getTotalUsdBase() { return totalUsdBase; }
    public void setTotalUsdBase(BigDecimal totalUsdBase) { this.totalUsdBase = totalUsdBase; }

    public BigDecimal getTipoCambioAplicado() { return tipoCambioAplicado; }
    public void setTipoCambioAplicado(BigDecimal tipoCambioAplicado) { this.tipoCambioAplicado = tipoCambioAplicado; }

    public String getWebpayToken() { return webpayToken; }
    public void setWebpayToken(String webpayToken) { this.webpayToken = webpayToken; }

    public String getWebpayStatus() { return webpayStatus; }
    public void setWebpayStatus(String webpayStatus) { this.webpayStatus = webpayStatus; }

    public Boolean getWebpayCallbackRecibido() { return webpayCallbackRecibido; }
    public void setWebpayCallbackRecibido(Boolean webpayCallbackRecibido) { this.webpayCallbackRecibido = webpayCallbackRecibido; }

    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
}
