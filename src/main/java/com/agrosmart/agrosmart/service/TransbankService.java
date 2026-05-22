package com.agrosmart.agrosmart.service;

import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.exception.TransactionCommitException;
import cl.transbank.webpay.exception.TransactionCreateException;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Servicio de integración con Transbank Webpay Plus.
 *
 * AMBIENTE: Integración (sandbox) — credenciales de prueba integradas en el SDK.
 *
 * Para PRODUCCIÓN, cambiar IntegrationType.TEST por IntegrationType.LIVE
 * y proporcionar las credenciales reales entregadas por Transbank:
 *   - commerceCode = tu código de comercio real
 *   - apiKey       = tu API key real
 */
@Service
public class TransbankService {

    private static final Logger log = LoggerFactory.getLogger(TransbankService.class);

    // ============================================================
    // CREDENCIALES — INTEGRACIÓN (SANDBOX)
    // ============================================================
    // Estas credenciales son públicas de Transbank para testing.
    // NO las cambies para ambiente de integración.
    private static final String COMMERCE_CODE_INTEGRACION = "597055555532";
    private static final String API_KEY_INTEGRACION        = "579B532A7440BB0C9079DED94D31EA1615BACEB56610332264630D42D0A36B1C";

    private final WebpayPlus.Transaction transaction;

    public TransbankService() {
        WebpayOptions options = new WebpayOptions(
                COMMERCE_CODE_INTEGRACION,
                API_KEY_INTEGRACION,
                IntegrationType.TEST
        );
        this.transaction = new WebpayPlus.Transaction(options);
    }

    // ============================================================
    // PASO 1: CREAR TRANSACCIÓN (antes de redirigir al banco)
    // ============================================================

    /**
     * Inicia una transacción en Transbank y devuelve el token + URL de pago.
     *
     * @param buyOrder  Orden de compra única (máx. 26 chars, sin espacios).
     *                  Usamos el ID de la Orden: "ORD-" + idOrden
     * @param sessionId Identificador de sesión del usuario (JSESSIONID u otro)
     * @param amount    Monto total en CLP (entero, sin decimales)
     * @param returnUrl URL a la que Transbank redirige tras el pago
     * @return Respuesta con token y url de Webpay
     */
    public WebpayPlusTransactionCreateResponse crearTransaccion(
            String buyOrder,
            String sessionId,
            long amount,
            String returnUrl) throws TransactionCreateException, IOException {

        log.info("[Transbank] Creando transacción — buyOrder={}, amount={}", buyOrder, amount);

        WebpayPlusTransactionCreateResponse response =
                transaction.create(buyOrder, sessionId, (double) amount, returnUrl);

        log.info("[Transbank] Token generado: {}", response.getToken());
        log.info("[Transbank] URL Webpay:    {}", response.getUrl());

        return response;
    }

    // ============================================================
    // PASO 2: CONFIRMAR TRANSACCIÓN (después de que el usuario vuelve)
    // ============================================================

    /**
     * Confirma la transacción con el token_ws recibido en el return URL.
     * Transbank valida el pago y devuelve el estado final.
     *
     * @param tokenWs Token recibido como parámetro GET/POST en el returnUrl
     * @return Respuesta de Transbank con el estado del pago
     */
    public WebpayPlusTransactionCommitResponse confirmarTransaccion(String tokenWs)
            throws TransactionCommitException, IOException {

        log.info("[Transbank] Confirmando transacción con token: {}", tokenWs);

        WebpayPlusTransactionCommitResponse response = transaction.commit(tokenWs);

        log.info("[Transbank] Estado: {}", response.getStatus());
        log.info("[Transbank] Código autorización: {}", response.getAuthorizationCode());

        return response;
    }

    // ============================================================
    // UTILIDADES
    // ============================================================

    /**
     * Genera un buyOrder único para Transbank a partir del ID de la orden.
     * Formato: "AGS-12345" (máx 26 chars, sin espacios ni caracteres especiales)
     */
    public static String generarBuyOrder(Integer idOrden) {
        return "AGS-" + idOrden;
    }
}
