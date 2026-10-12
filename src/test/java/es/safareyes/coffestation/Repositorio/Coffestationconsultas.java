package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Alergeno;
import es.safareyes.coffestation.Modelo.Cliente;
import es.safareyes.coffestation.Modelo.Pedido;
import es.safareyes.coffestation.Modelo.Producto;
import es.safareyes.coffestation.Modelo.ProductoPedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Ejecuta cada consulta contra la BD local e imprime lo que devuelve.
@SpringBootTest
public class Coffestationconsultas {

    @Autowired
    private Productorepo productorepo;

    @Autowired
    private Categoriarepo categoriarepo;

    @Autowired
    private Pedidorepo pedidorepo;

    @Autowired
    private Clienterepo clienterepo;

    // GET /api/v1/productos
    @Test
    void buscarProductosPorNombre() {

        Page<Producto> pagina =
                productorepo.findByNombreContainingIgnoreCaseAndActivoTrue("café", PageRequest.of(0, 10));

        System.out.println("Productos encontrados: " + pagina.getTotalElements());

        for (Producto producto : pagina.getContent()) {
            System.out.println("ID: " + producto.getId());
            System.out.println("Nombre: " + producto.getNombre());
            System.out.println("Precio: " + producto.getPrecio());
        }
    }

    // GET /api/v1/productos/{id}
    @Test
    void buscarProductoConAlergenos() {

        Optional<Producto> producto = productorepo.findWithAlergenosById(9L);

        if (producto.isPresent()) {
            System.out.println("Producto: " + producto.get().getNombre());
            for (Alergeno alergeno : producto.get().getAlergenos()) {
                System.out.println("Alérgeno: " + alergeno.getNombre());


            }

        } else {
            System.out.println("No existe el producto");
        }
    }

    // GET /api/v1/categorias
    @Test
    void buscarCategoriasConProductos() {

        // Cada Object[] es una categoría: [0] = nombre, [1] = nº de productos activos
        List<Object[]> categorias = categoriarepo.obtenerCategoriasConProductos();


        for (Object[] fila : categorias) {
            String nombreCategoria = (String) fila[0];
            Long productosActivos = (Long) fila[1];

            System.out.println("Categoría: " + nombreCategoria + " - Productos activos: " + productosActivos);
        }
    }


    // GET /api/v1/pedidos/{id}
    @Test
    void buscarPedidoConLineas() {

        Optional<Pedido> pedido = pedidorepo.obtenerPedidoConLineas(10L);

        if (pedido.isPresent()) {
            System.out.println("Pedido: " + pedido.get().getId() + " - Turno: " + pedido.get().getNumeroTurno());
            for (ProductoPedido linea : pedido.get().getLineas()) {
                System.out.println("Producto: " + linea.getProducto().getNombre() + " x " + linea.getCantidad());
            }

        } else {
            System.out.println("No existe el pedido");
        }
    }

    // GET /api/v1/pedidos
    @Test
    void buscarPedidosEntreFechas() {

        Page<Pedido> pagina = pedidorepo.findByFechaHoraBetween(
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 10, 2, 23, 59),
                PageRequest.of(0, 10));
        System.out.println("Pedidos encontrados: " + pagina.getTotalElements());

        for (Pedido pedido : pagina.getContent()) {
            System.out.println("ID: " + pedido.getId() + " - Fecha: " + pedido.getFechaHora());
        }
    }


    // GET /api/v1/informes/ventas-dia?fecha=
    @Test
    void buscarVentasDelDia() {

        LocalDate dia = LocalDate.of(2026, 10, 1);
        // El Object[] es el resumen del día: [0] = nº de pedidos, [1] = total, [2] = base, [3] = iva
        Object[] ventas = pedidorepo.obtenerVentasDia(dia.atStartOfDay(), dia.atTime(23, 59, 59)).get(0);

        Long numeroPedidos = (Long) ventas[0];
        BigDecimal total = (BigDecimal) ventas[1];
        BigDecimal base = (BigDecimal) ventas[2];
        BigDecimal iva = (BigDecimal) ventas[3];

        System.out.println("Pedidos: " + numeroPedidos);
        System.out.println("Total: " + total);
        System.out.println("Base: " + base);
        System.out.println("IVA: " + iva);
    }

    // GET /api/v1/informes/productos-top?desde=&hasta=&limite=
    @Test
    void buscarProductosMasVendidos() {


        // Cada Object[] es un producto: [0] = nombre, [1] = unidades vendidas
        List<Object[]> top = productorepo.obtenerProductosTop(
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 10, 3, 23, 59),
                5);

        for (Object[] fila : top) {
            String nombreProducto = (String) fila[0];
            Number unidadesVendidas = (Number) fila[1];
            System.out.println("Producto: " + nombreProducto + " - Unidades: " + unidadesVendidas);
        }
    }

    // EXTRA
    @Test
    void buscarProductosAgotados() {

        List<Producto> agotados = productorepo.findByDisponibleFalse();
        System.out.println("Productos agotados: " + agotados.size());
        for (Producto producto : agotados) {
            System.out.println("Nombre: " + producto.getNombre());
        }
    }


    // EXTRA
    @Test
    void buscarClientePorEmail() {

        Optional<Cliente> cliente = clienterepo.findByEmail("ana.lopez@correo.es");

        if (cliente.isPresent()) {
            System.out.println("Cliente: " + cliente.get().getNombre() + " - DNI: " + cliente.get().getDni());
        } else {
            System.out.println("No existe el cliente");
        }
    }
}
