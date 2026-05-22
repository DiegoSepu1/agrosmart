package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.entity.Orden;
import com.agrosmart.agrosmart.entity.Sucursal;
import com.agrosmart.agrosmart.model.Carrito;
import com.agrosmart.agrosmart.repository.SucursalRepository;
import com.agrosmart.agrosmart.service.CarritoService;
import com.agrosmart.agrosmart.service.OrdenService;
import com.agrosmart.agrosmart.service.TransbankService;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador del flujo de checkout.
 *
 * Rutas:
 *   GET  /checkout           → mostrar formulario de checkout
 *   POST /checkout/procesar  → validar, crear orden y redirigir a Transbank
 */
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private TransbankService transbankService;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private com.agrosmart.agrosmart.repository.AgricultorRepository agricultorRepository;

    // ============================================================
    // PASO 0: ELECCIÓN DE AUTENTICACIÓN (LOGIN / REGISTRO / INVITADO)
    // ============================================================

    @GetMapping("/auth")
    public String mostrarAuthChoice(HttpSession session, Model model) {
        Carrito carrito = carritoService.obtenerCarrito(session);
        if (carrito.isEmpty()) {
            return "redirect:/inventario";
        }
        return "auth-choice";
    }

    @GetMapping("/invitado")
    public String continuarComoInvitado(HttpSession session) {
        session.setAttribute("esInvitado", true);
        return "redirect:/checkout";
    }

    // ============================================================
    // PASO 1: MOSTRAR FORMULARIO DE CHECKOUT
    // ============================================================

    @GetMapping
    public String mostrarCheckout(HttpSession session, Model model,
                                RedirectAttributes redirectAttributes) {

        Carrito carrito = carritoService.obtenerCarrito(session);

        if (carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorCarrito",
                    "Tu carrito está vacío. Agrega productos antes de continuar.");
            return "redirect:/inventario";
        }

        // Verificar que el usuario esté logueado o haya elegido invitado
        Integer idAgricultor = (Integer) session.getAttribute("idAgricultor");
        Boolean esInvitado = (Boolean) session.getAttribute("esInvitado");

        if (idAgricultor == null && (esInvitado == null || !esInvitado)) {
            return "redirect:/checkout/auth";
        }

        // Si está logueado, intentar cargar sus datos para el formulario
        if (idAgricultor != null) {
            agricultorRepository.findById(idAgricultor).ifPresent(agri -> {
                model.addAttribute("agri", agri);
            });
        }

        // Cargar sucursales disponibles para el selector
        List<Sucursal> sucursales = sucursalRepository.findAll();

        model.addAttribute("carrito", carrito);
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("esInvitado", esInvitado != null && esInvitado);

        return "checkout";
    }

    // ============================================================
    // PASO 2: PROCESAR CHECKOUT → REDIRIGIR A TRANSBANK
    // ============================================================

    @PostMapping("/procesar")
    public String procesarCheckout(
            @RequestParam String tipoEntrega,
            @RequestParam(required = false) Integer idSucursal,
            @RequestParam(required = false) String direccionDespacho,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String rut,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // 1. Validar carrito
        Carrito carrito = carritoService.obtenerCarrito(session);
        if (carrito.isEmpty()) {
            log.warn("[Checkout] Intento de procesar checkout con carrito vacío.");
            redirectAttributes.addFlashAttribute("errorCarrito", "El carrito está vacío. Agrega productos antes de pagar.");
            return "redirect:/inventario";
        }

        Integer idAgricultor = (Integer) session.getAttribute("idAgricultor");
        Boolean esInvitado = (Boolean) session.getAttribute("esInvitado");

        // Validar que tengamos identificación (login o invitado)
        if (idAgricultor == null && (esInvitado == null || !esInvitado)) {
            log.warn("[Checkout] Intento de procesar checkout sin identificación (ni login ni invitado).");
            return "redirect:/checkout/auth";
        }

        try {
            log.info("[Checkout] Procesando pago para {} (ID Agricultor: {})", 
                    (esInvitado != null && esInvitado ? "Invitado" : "Usuario Registrado"), idAgricultor);
            
            // 2. Crear la orden en la BD
            Orden orden = ordenService.crearOrdenDesdeCarrito(
                    carrito, idAgricultor, tipoEntrega, idSucursal, direccionDespacho,
                    nombre, email, telefono, rut
            );

            log.info("[Checkout] Orden #{} creada exitosamente.", orden.getIdOrden());

            // 5. Construir la URL de retorno de Transbank (return_url)
            String serverName = request.getServerName();
            int serverPort    = request.getServerPort();
            String scheme     = request.getScheme();

            // Evitar duplicar puertos estándar en la URL
            String baseUrl;
            if (("http".equals(scheme) && serverPort == 80) || ("https".equals(scheme) && serverPort == 443)) {
                baseUrl = scheme + "://" + serverName;
            } else {
                baseUrl = scheme + "://" + serverName + ":" + serverPort;
            }
            
            String returnUrl = baseUrl + "/transbank/retorno";
            log.info("[Checkout] returnUrl: {}", returnUrl);

            // 6. Crear la transacción en Transbank
            String buyOrder   = TransbankService.generarBuyOrder(orden.getIdOrden());
            String sessionId  = session.getId();
            long   totalClp   = orden.getTotalClp().longValue();

            WebpayPlusTransactionCreateResponse tbResponse =
                    transbankService.crearTransaccion(buyOrder, sessionId, totalClp, returnUrl);

            if (tbResponse == null || tbResponse.getToken() == null) {
                throw new Exception("Transbank no devolvió un token válido.");
            }

            // 7. Guardar el token en la orden
            ordenService.guardarWebpayToken(orden.getIdOrden(), tbResponse.getToken());

            // 8. Redirigir al usuario a Transbank
            session.setAttribute("tbUrl",   tbResponse.getUrl());
            session.setAttribute("tbToken", tbResponse.getToken());

            return "redirect:/checkout/redirigir-banco";

        } catch (Exception e) {
            log.error("[Checkout] ERROR CRÍTICO: {}", e.getMessage(), e);
            
            String errorMsg = "Ocurrió un error al procesar tu pago: " + e.getMessage();
            if (e.getMessage().contains("id_agricultor")) {
                errorMsg = "Error de base de datos: La columna 'id_agricultor' no permite nulos. " +
                           "Asegúrate de haber ejecutado: ALTER TABLE orden MODIFY id_agricultor INT(11) NULL;";
            }
            
            redirectAttributes.addFlashAttribute("errorCheckout", errorMsg);
            return "redirect:/checkout";
        }
    }

    // ============================================================
    // VISTA INTERMEDIARIA: AUTO-SUBMIT A TRANSBANK
    // ============================================================

    /**
     * Esta vista renderiza un formulario HTML que se envía automáticamente al banco.
     * Transbank requiere un POST con el token, no un redirect GET.
     */
    @GetMapping("/redirigir-banco")
    public String redirigirAlBanco(HttpSession session, Model model,
                                    RedirectAttributes redirectAttributes) {

        String tbUrl   = (String) session.getAttribute("tbUrl");
        String tbToken = (String) session.getAttribute("tbToken");

        if (tbUrl == null || tbToken == null) {
            redirectAttributes.addFlashAttribute("errorCarrito", "Sesión de pago inválida.");
            return "redirect:/carrito";
        }

        // Limpiar atributos de sesión intermediarios
        session.removeAttribute("tbUrl");
        session.removeAttribute("tbToken");

        model.addAttribute("tbUrl",   tbUrl);
        model.addAttribute("tbToken", tbToken);

        return "redirigir-banco"; // renderiza auto-submit form
    }
}
