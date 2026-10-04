package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Pedido;
import es.safareyes.coffestation.Modelo.Producto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Prueba cada consulta de los repositorios contra la BD local.
// @Transactional deshace al final todo lo insertado: la BD queda como estaba.
// Usa ids 9001+, códigos TEST-… y el año 2030 para no chocar con los datos que ya tengas cargados.
@SpringBootTest
@Transactional
class ConsultasRepositoriosTest {

    @Autowired EntityManager em;
    @Autowired Productorepo productorepo;
    @Autowired Pedidorepo pedidorepo;
    @Autowired Cuponesrepo cuponesrepo;
    @Autowired Clienterepo clienterepo;
    @Autowired Categoriarepo categoriarepo;

    private static final LocalDate DIA = LocalDate.of(2030, 1, 15);

    // Las tablas no tienen ids autogenerados, así que los datos de prueba se insertan con SQL y con ids fijos
    @BeforeEach
    void insertarDatos() {
        sql("insert into coffestation.usuario (id, nombre, \"contraseña\", rol) values (9001, 'ana', 'x', 'cliente'), (9002, 'luis', 'x', 'cliente')");
        sql("insert into coffestation.cliente (id, nombre, email, dni, usuario_id) values (9001, 'Ana', 'ana@correo.es', '11111111H', 9001), (9002, 'Luis', 'luis@correo.es', '22222222J', 9002)");
        sql("insert into coffestation.categoria (id, nombre, disponible) values (9001, 'Cafés', true), (9002, 'Bollería', true), (9003, 'Zumos', true)");
        sql("insert into coffestation.alergeno (id, nombre) values (9001, 'Gluten'), (9002, 'Lactosa')");
        sql("insert into coffestation.producto (id, nombre, descripcion, precio, activo, disponible, categoria_id) values " +
                "(9001, 'Café con leche', 'Café y leche entera', 1.50, true, true, 9001), " +
                "(9002, 'Café solo', 'Espresso', 1.20, true, true, 9001), " +
                "(9003, 'Croissant', 'Croissant de mantequilla', 1.80, true, true, 9002), " +
                "(9004, 'Napolitana', 'Rellena de chocolate', 2.00, true, false, 9002), " +
                "(9005, 'Magdalena antigua', 'Retirada de la carta', 1.00, false, true, 9002)");
        sql("insert into coffestation.alergeno_producto (alergeno_id, producto_id) values (9002, 9001), (9001, 9003), (9002, 9003), (9001, 9004)");
        sql("insert into coffestation.pedido (id, numero_turno, fecha_hora, base, iva, cliente_id) values " +
                "(9001, 1, '2030-01-15 08:15', 2.73, 0.27, 9001), " +
                "(9002, 2, '2030-01-15 09:40', 4.55, 0.45, 9002), " +
                "(9003, 3, '2030-01-15 09:55', 1.09, 0.11, 9001)");
        sql("insert into coffestation.producto_pedido (producto_id, pedido_id, cantidad) values (9001, 9001, 2), (9003, 9002, 2), (9001, 9002, 1), (9002, 9003, 1)");
        sql("insert into coffestation.cupones (id, codigo, tipo, valor, fecha_inicio, fecha_fin, max_usos, importe_minimo, cliente_id) values " +
                "(9001, 'TEST-BIENVENIDA', 'porcentaje', 10.00, '2030-01-01', '2030-12-31', 2, null, 9001), " +
                "(9002, 'TEST-VERANO', 'importe', 1.00, '2029-06-01', '2029-08-31', 5, 3.00, 9001)");
        sql("insert into coffestation.pedido_cupones (pedido_id, cupon_id, descuento) values (9001, 9001, 0.30)");
    }

    private void sql(String sentencia) {
        em.createNativeQuery(sentencia).executeUpdate();
    }

    // ---------- Q1 derivadas ----------

    @Test
    void q1_derivadas() {
        assertThat(productorepo.findByCategoria_IdAndActivoTrue(9002L)).hasSize(2);           // sin la dada de baja
        assertThat(productorepo.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase("CAFÉ", "café"))
                .extracting(Producto::getId).contains(9001L, 9002L).doesNotContain(9003L);
        assertThat(productorepo.findByPrecioBetweenAndDisponibleTrueOrderByPrecioAsc(new BigDecimal("1.00"), new BigDecimal("1.60")))
                .extracting(Producto::getNombre).containsSubsequence("Café solo", "Café con leche");
        assertThat(productorepo.countByCategoria_IdAndActivoTrue(9002L)).isEqualTo(2);
        assertThat(cuponesrepo.existsByCodigoIgnoreCase("test-bienvenida")).isTrue();
        assertThat(cuponesrepo.findFirstByCodigoIgnoreCase("test-verano")).isPresent();
        assertThat(clienterepo.findTopByEmailIgnoreCase("ANA@correo.es")).isPresent();
        assertThat(clienterepo.existsByDni("22222222J")).isTrue();
    }

    // ---------- Q2 JPQL ----------

    @Test
    void q2_jpql() {
        assertThat(productorepo.obtenerProductosSinAlergeno(9001L)).extracting(Producto::getId).doesNotContain(9003L, 9004L);
        assertThat(pedidorepo.obtenerPedidosPorEmail("ana@correo.es")).hasSize(2);
        assertThat(pedidorepo.contarPedidosEntre(DIA.atStartOfDay(), DIA.plusDays(1).atStartOfDay())).isEqualTo(3);
        assertThat(pedidorepo.sumarVentasEntre(DIA.atStartOfDay(), DIA.plusDays(1).atStartOfDay())).isEqualByComparingTo("9.20");
    }

    // ---------- Q3 nativas ----------

    @Test
    void q3_nativas() {
        // fila = {id del producto, nombre, unidades, importe}
        List<Object[]> top = productorepo.obtenerProductosTop(DIA.atStartOfDay(), DIA.atStartOfDay(), 2);
        assertThat(top).hasSize(2);
        assertThat(top.get(0)[1]).isEqualTo("Café con leche");
        assertThat(((Number) top.get(0)[2]).longValue()).isEqualTo(3L);

        // fila = {hora, número de pedidos, total}
        List<Object[]> porHora = pedidorepo.obtenerVentasPorHora(DIA);
        assertThat(porHora).extracting(fila -> ((Number) fila[0]).intValue()).containsExactly(8, 9);
        assertThat(((Number) porHora.get(1)[1]).longValue()).isEqualTo(2L);
    }

    // ---------- Q4 modificación ----------

    @Test
    void q4_modificacion() {
        int filas = productorepo.cambiarDisponibilidadCategoria(9002L, false);
        assertThat(filas).isEqualTo(3);
        assertThat(productorepo.findById(9003L).orElseThrow().getDisponible()).isFalse();
    }

    // ---------- Consultas que devuelven Object[] ----------

    @Test
    void resumenes() {
        // fila = {id, nombre, precio, disponible, categoría}
        List<Object[]> carta = productorepo.obtenerCarta();
        assertThat(carta).extracting(fila -> fila[1]).contains("Croissant").doesNotContain("Magdalena antigua");

        // una sola fila = {número de pedidos, total, base, iva}
        Object[] ventas = pedidorepo.obtenerVentasDia(DIA.atStartOfDay(), DIA.plusDays(1).atStartOfDay()).get(0);
        assertThat(((Number) ventas[0]).longValue()).isEqualTo(3L);
        assertThat((BigDecimal) ventas[1]).isEqualByComparingTo("9.20");

        // fila = {id, nombre, número de productos activos}
        List<Object[]> categorias = categoriarepo.obtenerCategoriasConProductosActivos();
        assertThat(categorias).filteredOn(fila -> fila[0].equals(9002L)).extracting(fila -> fila[2]).containsExactly(2L);
        assertThat(categorias).filteredOn(fila -> fila[0].equals(9003L)).extracting(fila -> fila[2]).containsExactly(0L);
    }

    // ---------- Q6 paginación ----------

    @Test
    void q6_paginacion() {
        var pagina = productorepo.findByActivoTrue(PageRequest.of(0, 2, Sort.by("precio")));
        assertThat(pagina.getContent()).hasSize(2);
        assertThat(pagina.getTotalElements()).isGreaterThanOrEqualTo(4);

        assertThat(pedidorepo.findByCliente_Id(9001L, PageRequest.of(0, 10)).getTotalElements()).isEqualTo(2);
    }

    // ---------- Q8 N+1 ----------

    @Test
    void q8_entityGraph() {
        Pedido pedido = pedidorepo.obtenerPedidoConLineas(9002L).orElseThrow();
        em.detach(pedido);   // fuera de la sesión: si las líneas no estuvieran cargadas, fallaría al leerlas
        assertThat(pedido.getLineas()).hasSize(2);
        assertThat(pedido.getLineas()).extracting(l -> l.getProducto().getNombre()).contains("Croissant");
    }

    // ---------- Extras ----------

    @Test
    void extras() {
        assertThat(productorepo.obtenerProductosNuncaVendidos()).extracting(Producto::getId).contains(9004L).doesNotContain(9001L);

        // fila = {id del cliente, email, número de pedidos, total gastado}
        List<Object[]> mejores = pedidorepo.obtenerMejoresClientes(100);
        assertThat(mejores).extracting(fila -> fila[1]).containsSubsequence("luis@correo.es", "ana@correo.es");

        assertThat(cuponesrepo.obtenerCuponesVigentesDeCliente(9001L, LocalDateTime.of(2030, 1, 15, 10, 0)))
                .extracting(c -> c.getCodigo()).containsExactly("TEST-BIENVENIDA");
    }
}
