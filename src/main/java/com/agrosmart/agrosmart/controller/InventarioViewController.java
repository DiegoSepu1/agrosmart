package com.agrosmart.agrosmart.controller;

import com.agrosmart.agrosmart.entity.DetalleOrden;
import com.agrosmart.agrosmart.entity.Orden;
import com.agrosmart.agrosmart.entity.Producto;
import com.agrosmart.agrosmart.repository.DetalleOrdenRepository;
import com.agrosmart.agrosmart.repository.OrdenRepository;
import com.agrosmart.agrosmart.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/inventario")
public class InventarioViewController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @GetMapping
    public String verInventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario";
    }

    @GetMapping("/admin")
    public String verPanelAdmin(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin";
    }

    @GetMapping("/informes")
    @Transactional(readOnly = true)
    public String verInformesVentas(Model model) {
        List<Orden> ordenes = ordenRepository.findAllByOrderByFechaCreacionDesc();
        List<Producto> productos = productoRepository.findAll();
        List<DetalleOrden> detalles = detalleOrdenRepository.findAll();

        // Mapa id_producto -> nombre para enriquecer reportes
        Map<Integer, String> nombrePorProducto = productos.stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre, (a, b) -> a));

        // ====== KPIs DE ÓRDENES ======
        int totalOrdenes = ordenes.size();
        int ordenesAprobadas = 0;
        int ordenesPendientes = 0;
        int ordenesCanceladas = 0;

        BigDecimal totalVentasClp = BigDecimal.ZERO;
        BigDecimal totalVentasUsd = BigDecimal.ZERO;
        BigDecimal ventasMesActual = BigDecimal.ZERO;

        Set<String> clientesUnicos = new HashSet<>();
        Set<Integer> idOrdenesAprobadas = new HashSet<>();

        int retiroCount = 0;
        int despachoCount = 0;

        YearMonth mesActual = YearMonth.now();

        // Estructura para ventas de los últimos 6 meses (CLP)
        Map<YearMonth, BigDecimal> ventasPorMes = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            ventasPorMes.put(mesActual.minusMonths(i), BigDecimal.ZERO);
        }

        for (Orden orden : ordenes) {
            boolean aprobada = "pago_confirmado".equals(orden.getEstado())
                    || "aprobado".equals(orden.getWebpayStatus());
            boolean cancelada = "cancelada".equals(orden.getEstado())
                    || "rechazado".equals(orden.getWebpayStatus());

            if (aprobada) {
                ordenesAprobadas++;
                idOrdenesAprobadas.add(orden.getIdOrden());

                if (orden.getTotalClp() != null) {
                    totalVentasClp = totalVentasClp.add(orden.getTotalClp());
                }
                if (orden.getTotalUsdBase() != null) {
                    totalVentasUsd = totalVentasUsd.add(orden.getTotalUsdBase());
                }

                // Cliente único (por email; si no hay, por id de agricultor)
                if (orden.getClienteEmail() != null && !orden.getClienteEmail().isBlank()) {
                    clientesUnicos.add(orden.getClienteEmail().toLowerCase());
                } else if (orden.getIdAgricultor() != null) {
                    clientesUnicos.add("agri:" + orden.getIdAgricultor());
                }

                // Tipo de entrega
                if ("retiro_programado".equals(orden.getTipoEntrega())) {
                    retiroCount++;
                } else if ("despacho_predio".equals(orden.getTipoEntrega())) {
                    despachoCount++;
                }

                // Ventas por mes + mes actual
                LocalDateTime fecha = orden.getFechaCreacion();
                if (fecha != null && orden.getTotalClp() != null) {
                    YearMonth ym = YearMonth.from(fecha);
                    if (ventasPorMes.containsKey(ym)) {
                        ventasPorMes.put(ym, ventasPorMes.get(ym).add(orden.getTotalClp()));
                    }
                    if (ym.equals(mesActual)) {
                        ventasMesActual = ventasMesActual.add(orden.getTotalClp());
                    }
                }
            } else if (cancelada) {
                ordenesCanceladas++;
            } else {
                ordenesPendientes++;
            }
        }

        // Ticket promedio (CLP) sobre órdenes aprobadas
        BigDecimal ticketPromedio = ordenesAprobadas > 0
                ? totalVentasClp.divide(BigDecimal.valueOf(ordenesAprobadas), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Tasa de conversión (% aprobadas sobre el total)
        double tasaConversion = totalOrdenes > 0
                ? (ordenesAprobadas * 100.0) / totalOrdenes
                : 0.0;

        // ====== PRODUCTOS MÁS VENDIDOS (solo órdenes aprobadas) ======
        Map<Integer, int[]> unidadesPorProducto = new HashMap<>();       // id -> [unidades]
        Map<Integer, BigDecimal> ingresosPorProducto = new HashMap<>();  // id -> ingresos CLP
        int unidadesVendidas = 0;

        for (DetalleOrden detalle : detalles) {
            Integer idOrden = detalle.getOrden() != null ? detalle.getOrden().getIdOrden() : null;
            if (idOrden == null || !idOrdenesAprobadas.contains(idOrden)) {
                continue;
            }
            int cantidad = detalle.getCantidad() != null ? detalle.getCantidad() : 0;
            unidadesVendidas += cantidad;

            unidadesPorProducto.computeIfAbsent(detalle.getIdProducto(), k -> new int[1])[0] += cantidad;

            BigDecimal precio = detalle.getPrecioUnitarioClp() != null
                    ? detalle.getPrecioUnitarioClp() : BigDecimal.ZERO;
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            ingresosPorProducto.merge(detalle.getIdProducto(), subtotal, BigDecimal::add);
        }

        List<ProductoVendido> topProductos = unidadesPorProducto.entrySet().stream()
                .map(e -> new ProductoVendido(
                        nombrePorProducto.getOrDefault(e.getKey(), "Producto #" + e.getKey()),
                        e.getValue()[0],
                        ingresosPorProducto.getOrDefault(e.getKey(), BigDecimal.ZERO)))
                .sorted(Comparator.comparingInt(ProductoVendido::unidades).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // ====== STOCK ======
        int totalProductos = productos.size();
        long productosSinStock = productos.stream()
                .filter(p -> p.getStockFisico() == null || p.getStockFisico() <= 0)
                .count();
        long productosBajoStock = productos.stream()
                .filter(p -> p.getStockFisico() != null && p.getStockFisico() > 0 && p.getStockFisico() <= 5)
                .count();

        // ====== DATOS PARA GRÁFICOS (listas paralelas) ======
        List<String> mesesLabels = new ArrayList<>();
        List<Long> mesesData = new ArrayList<>();
        for (Map.Entry<YearMonth, BigDecimal> e : ventasPorMes.entrySet()) {
            String etiqueta = e.getKey().getMonth()
                    .getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            mesesLabels.add(etiqueta + " " + e.getKey().getYear());
            mesesData.add(e.getValue().longValue());
        }

        // ====== ÚLTIMAS ÓRDENES ======
        List<Orden> ultimasOrdenes = ordenes.stream().limit(10).collect(Collectors.toList());

        // ====== EXPONER AL MODELO ======
        model.addAttribute("totalOrdenes", totalOrdenes);
        model.addAttribute("ordenesAprobadas", ordenesAprobadas);
        model.addAttribute("ordenesPendientes", ordenesPendientes);
        model.addAttribute("ordenesCanceladas", ordenesCanceladas);
        model.addAttribute("totalVentasClp", totalVentasClp.longValue());
        model.addAttribute("totalVentasUsd", totalVentasUsd.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("ventasMesActual", ventasMesActual.longValue());
        model.addAttribute("ticketPromedio", ticketPromedio.longValue());
        model.addAttribute("tasaConversion", String.format(Locale.US, "%.1f", tasaConversion));
        model.addAttribute("clientesUnicos", clientesUnicos.size());
        model.addAttribute("unidadesVendidas", unidadesVendidas);

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("productosSinStock", productosSinStock);
        model.addAttribute("productosBajoStock", productosBajoStock);

        model.addAttribute("topProductos", topProductos);
        model.addAttribute("ultimasOrdenes", ultimasOrdenes);

        model.addAttribute("retiroCount", retiroCount);
        model.addAttribute("despachoCount", despachoCount);

        model.addAttribute("mesesLabels", mesesLabels);
        model.addAttribute("mesesData", mesesData);

        model.addAttribute("productos", productos);

        return "informes";
    }

    /** DTO para la tabla de productos más vendidos. */
    public record ProductoVendido(String nombre, int unidades, BigDecimal ingresos) {
        public int getUnidades() { return unidades; }
        public String getNombre() { return nombre; }
        public BigDecimal getIngresos() { return ingresos; }
    }
}
