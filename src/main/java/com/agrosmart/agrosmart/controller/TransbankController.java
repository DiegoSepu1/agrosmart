package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.entity.Orden;
import com.agrosmart.agrosmart.service.CarritoService;
import com.agrosmart.agrosmart.service.OrdenService;
import com.agrosmart.agrosmart.service.TransbankService;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador del retorno de Transbank Webpay Plus.
 *
 * Transbank redirige al usuario de vuelta aquí después del pago.
 * La URL de retorno configurada en CheckoutController es: /transbank/retorno
 *
 * Webpay envía el token_ws como parámetro POST.
 * Si el usuario CANCELA el pago, Webpay envía TBK_TOKEN, TBK_ORDEN_COMPRA, TBK_ID_SESION.
 */
@Controller
@RequestMapping("/transbank")
public class TransbankController {

    private static final Logger log = LoggerFactory.getLogger(TransbankController.class);

    @Autowired
    private TransbankService transbankService;

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private CarritoService carritoService;

    // ============================================================
    // RETORNO DESDE TRANSBANK
    // ============================================================

    /**
     * Transbank llama a este endpoint después de que el usuario completa (o cancela) el pago.
     *
     * Casos posibles:
     * A) Pago aprobado:   token_ws = TOKEN_APROBADO
     * B) Pago rechazado:  token_ws = TOKEN_RECHAZADO (Transbank lo marca como rechazado)
     * C) Timeout/Cancelación por usuario: TBK_TOKEN + TBK_ORDEN_COMPRA + TBK_ID_SESION (sin token_ws)
     * D) Doble envío:     token_ws + TBK_TOKEN (no confirmar, el usuario volvió atrás)
     */
    // ⬇️ ESTA ES LA ÚNICA LÍNEA QUE CAMBIA ⬇️
    @RequestMapping("/retorno")
    public String retornoTransbank(
            @RequestParam(value = "token_ws",        required = false) String tokenWs,
            @RequestParam(value = "TBK_TOKEN",       required = false) String tbkToken,
            @RequestParam(value = "TBK_ORDEN_COMPRA",required = false) String tbkOrdenCompra,
            @RequestParam(value = "TBK_ID_SESION",   required = false) String tbkIdSesion,
            HttpSession session,
            Model model) {

        // ---- CASO C: El usuario canceló (timeout o botón atrás en Webpay) ----
        if (tokenWs == null && tbkToken != null) {
            log.warn("[Transbank] Pago cancelado por el usuario. TBK_ORDEN_COMPRA={}", tbkOrdenCompra);

            // Marcar la orden como cancelada
            if (tbkOrdenCompra != null) {
                try {
                    String idOrdenStr = tbkOrdenCompra.replace("AGS-", "");
                    Integer idOrden = Integer.parseInt(idOrdenStr);
                    ordenService.cancelarOrden(idOrden);
                } catch (NumberFormatException ignored) {}
            }

            model.addAttribute("razonFallo", "El pago fue cancelado por el usuario.");
            return "pago-fallido";
        }

        // ---- CASO D: Double POST (usuario volvió atrás con el navegador) ----
        if (tokenWs != null && tbkToken != null) {
            log.warn("[Transbank] Doble envío detectado. NO confirmar. token_ws={}", tokenWs);
            model.addAttribute("razonFallo", "Sesión de pago inválida. Por favor inicia el proceso nuevamente.");
            return "pago-fallido";
        }

        // ---- CASO A o B: Pago completado (aprobado o rechazado) ----
        if (tokenWs == null) {
            log.error("[Transbank] Retorno sin token_ws ni TBK_TOKEN. Parámetros inesperados.");
            model.addAttribute("razonFallo", "Respuesta inesperada del banco.");
            return "pago-fallido";
        }

        try {
            // Confirmar la transacción con el SDK de Transbank
            WebpayPlusTransactionCommitResponse response =
                    transbankService.confirmarTransaccion(tokenWs);

            log.info("[Transbank] Status: {}  |  Código auth: {}  |  Monto: {}",
                    response.getStatus(), response.getAuthorizationCode(), response.getAmount());

            boolean aprobado = "AUTHORIZED".equals(response.getStatus());

            // Actualizar la orden y registrar el pago
            Orden orden = ordenService.confirmarPago(
                    tokenWs, aprobado, response.getAuthorizationCode());

            if (aprobado) {
                // Vaciar el carrito solo si el pago fue exitoso
                carritoService.vaciarCarrito(session);

                model.addAttribute("orden",        orden);
                model.addAttribute("codigoAuth",   response.getAuthorizationCode());
                model.addAttribute("montoAprobado",response.getAmount());
                model.addAttribute("ultimos4",     response.getCardDetail() != null
                        ? response.getCardDetail().getCardNumber() : "****");
                return "pago-exitoso";

            } else {
                model.addAttribute("razonFallo",
                        "El pago fue rechazado por el banco. Código: " + response.getStatus());
                return "pago-fallido";
            }

        } catch (Exception e) {
            log.error("[Transbank] Error al confirmar transacción: {}", e.getMessage(), e);
            model.addAttribute("razonFallo",
                    "Error al confirmar el pago con el banco. Por favor contacta soporte.");
            return "pago-fallido";
        }
    }
}
