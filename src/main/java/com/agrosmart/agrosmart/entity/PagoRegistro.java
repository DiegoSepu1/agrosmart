package com.agrosmart.agrosmart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapea la tabla 'pago_registro'.
 * Registra cada intento/confirmación de pago, incluyendo la respuesta de Transbank.
 */
@Entity
@Table(name = "pago_registro")
public class PagoRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    /** FK a la orden asociada */
    @Column(name = "id_orden", nullable = false)
    private Integer idOrden;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    /** ID de transacción en Transbank (authorization code o buy_order) */
    @Column(name = "transaccion_id_externo", length = 255)
    private String transaccionIdExterno;

    /**
     * Estado del pago (ENUM en BD):
     * iniciado | aprobado | rechazado | anulado
     */
    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago;

    @Column(name = "webhook_recibido")
    private LocalDateTime webhookRecibido;

    /** FK al usuario de cobranzas que confirma manualmente (opcional) */
    @Column(name = "id_cobranzas_confirma")
    private Integer idCobranzasConfirma;

    @PrePersist
    public void prePersist() {
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
    }

    public PagoRegistro() {}

    // ========== GETTERS Y SETTERS ==========
    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Integer getIdOrden() { return idOrden; }
    public void setIdOrden(Integer idOrden) { this.idOrden = idOrden; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public String getTransaccionIdExterno() { return transaccionIdExterno; }
    public void setTransaccionIdExterno(String transaccionIdExterno) { this.transaccionIdExterno = transaccionIdExterno; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public LocalDateTime getWebhookRecibido() { return webhookRecibido; }
    public void setWebhookRecibido(LocalDateTime webhookRecibido) { this.webhookRecibido = webhookRecibido; }

    public Integer getIdCobranzasConfirma() { return idCobranzasConfirma; }
    public void setIdCobranzasConfirma(Integer idCobranzasConfirma) { this.idCobranzasConfirma = idCobranzasConfirma; }
}
