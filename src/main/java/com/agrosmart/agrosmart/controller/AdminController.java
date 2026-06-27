package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.config.Roles;
import com.agrosmart.agrosmart.entity.Agricultor;
import com.agrosmart.agrosmart.entity.DetalleOrden;
import com.agrosmart.agrosmart.entity.Orden;
import com.agrosmart.agrosmart.entity.PagoRegistro;
import com.agrosmart.agrosmart.entity.Producto;
import com.agrosmart.agrosmart.entity.Usuario;
import com.agrosmart.agrosmart.repository.AgricultorRepository;
import com.agrosmart.agrosmart.repository.DetalleOrdenRepository;
import com.agrosmart.agrosmart.repository.OrdenRepository;
import com.agrosmart.agrosmart.repository.PagoRegistroRepository;
import com.agrosmart.agrosmart.repository.ProductoRepository;
import com.agrosmart.agrosmart.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Panel de administración (solo accesible con perfil admin; protegido por
 * {@link com.agrosmart.agrosmart.config.SessionAuthInterceptor}).
 *
 * Cubre los requisitos del profesor:
 *   - 1.2  CRUD de clientes (listar / ver detalle / editar / eliminar).
 *   - 5.2  El admin accede a administración de clientes, ventas y pagos.
 *
 * Reusa las entidades y repositorios existentes; NO modifica el flujo de compra.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AgricultorRepository agricultorRepository;
    @Autowired private OrdenRepository ordenRepository;
    @Autowired private DetalleOrdenRepository detalleOrdenRepository;
    @Autowired private PagoRegistroRepository pagoRegistroRepository;
    @Autowired private ProductoRepository productoRepository;

    // ============================================================
    // CLIENTES (requisito 1.2 y 5.2)
    // ============================================================

    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        List<Usuario> usuarios = usuarioRepository.findByRolOrderByIdUsuarioDesc(Roles.CLIENTE);
        List<ClienteView> clientes = new ArrayList<>();
        for (Usuario u : usuarios) {
            Agricultor agri = agricultorRepository.findByIdUsuario(u.getIdUsuario()).orElse(null);
            clientes.add(new ClienteView(u, agri));
        }
        model.addAttribute("clientes", clientes);
        return "admin-clientes";
    }

    @GetMapping("/clientes/{id}")
    public String verCliente(@PathVariable Integer id, Model model,
                             RedirectAttributes ra) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorAdmin", "Cliente no encontrado.");
            return "redirect:/admin/clientes";
        }
        Usuario u = opt.get();
        Agricultor agri = agricultorRepository.findByIdUsuario(u.getIdUsuario()).orElse(null);
        model.addAttribute("cliente", new ClienteView(u, agri));
        return "admin-cliente-detalle";
    }

    @PostMapping("/clientes/{id}/editar")
    @Transactional
    public String editarCliente(@PathVariable Integer id,
                                @RequestParam String nombre,
                                @RequestParam(required = false) String telefono,
                                @RequestParam(required = false) String rut,
                                @RequestParam(required = false) String direccion,
                                RedirectAttributes ra) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorAdmin", "Cliente no encontrado.");
            return "redirect:/admin/clientes";
        }
        Usuario u = opt.get();
        u.setNombreCompleto(nombre);
        u.setTelefono(telefono);
        usuarioRepository.save(u);

        agricultorRepository.findByIdUsuario(u.getIdUsuario()).ifPresent(agri -> {
            if (rut != null && !rut.isBlank()) agri.setRut(rut);
            agri.setRazonSocial(nombre);
            agri.setDireccionPredio(direccion);
            agricultorRepository.save(agri);
        });

        ra.addFlashAttribute("exitoAdmin", "Cliente actualizado correctamente.");
        return "redirect:/admin/clientes/" + id;
    }

    /**
     * Baja lógica: se desactiva el usuario (activo=false) en lugar de borrarlo,
     * para no romper la integridad referencial con órdenes ya emitidas.
     */
    @PostMapping("/clientes/{id}/eliminar")
    @Transactional
    public String eliminarCliente(@PathVariable Integer id, RedirectAttributes ra) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setActivo(false);
            usuarioRepository.save(u);
        });
        ra.addFlashAttribute("exitoAdmin", "Cliente desactivado (baja lógica).");
        return "redirect:/admin/clientes";
    }

    // ============================================================
    // VENTAS (requisito 5.2)
    // ============================================================

    @GetMapping("/ventas")
    public String listarVentas(Model model) {
        List<Orden> ventas = ordenRepository.findAllByOrderByFechaCreacionDesc();
        model.addAttribute("ventas", ventas);
        return "admin-ventas";
    }

    @GetMapping("/ventas/{id}")
    @Transactional(readOnly = true)
    public String verVenta(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Orden> opt = ordenRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorAdmin", "Venta no encontrada.");
            return "redirect:/admin/ventas";
        }
        Orden orden = opt.get();
        List<DetalleOrden> detalles = detalleOrdenRepository.findByOrden_IdOrden(id);

        // Nombres de producto para la tabla de detalle
        Map<Integer, String> nombrePorProducto = productoRepository.findAll().stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre, (a, b) -> a));

        // Pagos asociados a esta orden
        List<PagoRegistro> pagos = pagoRegistroRepository.findAll().stream()
                .filter(p -> id.equals(p.getIdOrden()))
                .collect(Collectors.toList());

        model.addAttribute("orden", orden);
        model.addAttribute("detalles", detalles);
        model.addAttribute("nombrePorProducto", nombrePorProducto);
        model.addAttribute("pagos", pagos);
        return "admin-venta-detalle";
    }

    // ============================================================
    // PAGOS (requisito 5.2)
    // ============================================================

    @GetMapping("/pagos")
    public String listarPagos(Model model) {
        List<PagoRegistro> pagos = pagoRegistroRepository.findAll();
        pagos.sort((a, b) -> {
            if (a.getIdPago() == null || b.getIdPago() == null) return 0;
            return b.getIdPago().compareTo(a.getIdPago());
        });
        model.addAttribute("pagos", pagos);
        return "admin-pagos";
    }

    // ============================================================
    // DTO para la vista de clientes
    // ============================================================

    /** Agrupa el Usuario (login) con su ficha de Agricultor (datos comerciales). */
    public static class ClienteView {
        private final Usuario usuario;
        private final Agricultor agricultor;

        public ClienteView(Usuario usuario, Agricultor agricultor) {
            this.usuario = usuario;
            this.agricultor = agricultor;
        }

        public Usuario getUsuario() { return usuario; }
        public Agricultor getAgricultor() { return agricultor; }
    }
}
