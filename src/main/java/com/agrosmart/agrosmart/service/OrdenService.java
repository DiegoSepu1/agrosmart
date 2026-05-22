package com.agrosmart.agrosmart.service;

import com.agrosmart.agrosmart.entity.DetalleOrden;
import com.agrosmart.agrosmart.entity.Orden;
import com.agrosmart.agrosmart.entity.PagoRegistro;
import com.agrosmart.agrosmart.model.Carrito;
import com.agrosmart.agrosmart.model.CarritoItem;
import com.agrosmart.agrosmart.repository.OrdenRepository;
import com.agrosmart.agrosmart.repository.PagoRegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Servicio que convierte el Carrito en una Orden persistida en MySQL
 * y gestiona el estado de pago.
 */
@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private PagoRegistroRepository pagoRegistroRepository;

    // ============================================================
    // CREAR ORDEN DESDE CARRITO
    // ============================================================

    /**
     * Persiste una nueva Orden con sus DetalleOrden a partir del carrito.
     *
     * @param carrito        El carrito actual del usuario
     * @param idAgricultor   ID del agricultor comprador (viene de la sesión de login)
     * @param tipoEntrega    "retiro_programado" o "despacho_predio"
     * @param idSucursal     ID de sucursal (null si es despacho a predio)
     * @param direccionDespacho Dirección (null si es retiro en sucursal)
     * @return La orden creada (con su ID asignado)
     */
    @Transactional
    public Orden crearOrdenDesdeCarrito(Carrito carrito, Integer idAgricultor,
                                        String tipoEntrega, Integer idSucursal,
                                        String direccionDespacho,
                                        String nombre, String email, String telefono, String rut) {

        if (carrito == null || carrito.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        // 1. Calcular tipo de cambio (usar el del primer ítem como referencia)
        Double tipoCambioRef = carrito.getItems().stream()
                .filter(i -> i.getTipoCambioUsado() != null)
                .map(CarritoItem::getTipoCambioUsado)
                .findFirst()
                .orElse(1.0);

        BigDecimal tipoCambio = BigDecimal.valueOf(tipoCambioRef).setScale(2, RoundingMode.HALF_UP);

        // 2. Calcular totales
        BigDecimal totalClp = BigDecimal.valueOf(carrito.getTotalClp());
        BigDecimal totalUsd = BigDecimal.valueOf(carrito.getTotalUsd()).setScale(2, RoundingMode.HALF_UP);

        // 3. Construir la Orden
        Orden orden = new Orden();
        orden.setIdAgricultor(idAgricultor);
        orden.setClienteNombre(nombre);
        orden.setClienteEmail(email);
        orden.setClienteTelefono(telefono);
        orden.setClienteRut(rut);
        orden.setEstado("pago_pendiente");
        orden.setMetodoPago("transferencia_webpay");
        orden.setTipoEntrega(tipoEntrega);
        orden.setIdSucursalRetiro(idSucursal);
        orden.setDireccionDespacho(direccionDespacho);
        orden.setTotalClp(totalClp);
        orden.setTotalUsdBase(totalUsd);
        orden.setTipoCambioAplicado(tipoCambio);
        orden.setWebpayStatus("inicial");
        orden.setWebpayCallbackRecibido(false);

        // 4. Construir los DetalleOrden desde los ítems del carrito
        for (CarritoItem item : carrito.getItems()) {
            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(orden);
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitarioClp(
                    BigDecimal.valueOf(item.getPrecioUnitarioClp()).setScale(2, RoundingMode.HALF_UP));
            detalle.setPrecioUnitarioUsd(
                    BigDecimal.valueOf(item.getPrecioUnitarioUsd()).setScale(2, RoundingMode.HALF_UP));
            // Sucursal origen: la misma de retiro, o 1 por defecto si es despacho
            detalle.setIdSucursalOrigen(idSucursal != null ? idSucursal : 1);
            orden.getDetalles().add(detalle);
        }

        // 5. Guardar orden + detalles (cascade = ALL)
        return ordenRepository.save(orden);
    }

    // ============================================================
    // GUARDAR TOKEN DE TRANSBANK EN LA ORDEN
    // ============================================================

    @Transactional
    public void guardarWebpayToken(Integer idOrden, String token) {
        ordenRepository.findById(idOrden).ifPresent(orden -> {
            orden.setWebpayToken(token);
            ordenRepository.save(orden);
        });
    }

    // ============================================================
    // CONFIRMAR PAGO (llamado desde TransbankController)
    // ============================================================

    /**
     * Actualiza la orden y registra el pago tras recibir la respuesta de Transbank.
     *
     * @param token              El token_ws devuelto por Transbank
     * @param aprobado           true si el pago fue aprobado
     * @param transaccionExterna ID externo de la transacción (authorization code)
     * @return La orden actualizada
     */
    @Transactional
    public Orden confirmarPago(String token, boolean aprobado, String transaccionExterna) {
        Optional<Orden> opt = ordenRepository.findByWebpayToken(token);

        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró una orden con el token: " + token);
        }

        Orden orden = opt.get();
        orden.setWebpayCallbackRecibido(true);

        if (aprobado) {
            orden.setWebpayStatus("aprobado");
            orden.setEstado("pago_confirmado");
        } else {
            orden.setWebpayStatus("rechazado");
            orden.setEstado("cancelada");
        }

        ordenRepository.save(orden);

        // Registrar el pago en la tabla pago_registro
        PagoRegistro pago = new PagoRegistro();
        pago.setIdOrden(orden.getIdOrden());
        pago.setMonto(orden.getTotalClp());
        pago.setEstadoPago(aprobado ? "aprobado" : "rechazado");
        pago.setTransaccionIdExterno(transaccionExterna);
        pagoRegistroRepository.save(pago);

        return orden;
    }

    // ============================================================
    // CANCELAR ORDEN (llamado cuando el usuario cancela en Webpay)
    // ============================================================

    @Transactional
    public void cancelarOrden(Integer idOrden) {
        ordenRepository.findById(idOrden).ifPresent(o -> {
            o.setEstado("cancelada");
            o.setWebpayStatus("rechazado");
            o.setWebpayCallbackRecibido(true);
            ordenRepository.save(o);
        });
    }

    // ============================================================
    // OBTENER ORDEN POR ID
    // ============================================================

    public Optional<Orden> obtenerPorId(Integer idOrden) {
        return ordenRepository.findById(idOrden);
    }

    public Optional<Orden> obtenerPorToken(String token) {
        return ordenRepository.findByWebpayToken(token);
    }
}
