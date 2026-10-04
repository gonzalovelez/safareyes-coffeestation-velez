package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface Pedidorepo extends JpaRepository<Pedido, Long> {

    //PAGINACIÓN (Q6): pedidos de un cliente, para que un CLIENTE solo vea los suyos (endpoint 11)
    Page<Pedido> findByCliente_Id(Long clienteId, Pageable pageable);

    //JPQL (Q2)

    // Pedidos creados en un intervalo. Sirve para el número de turno del día (RN-09): turno = count + 1
    @Query("select count(p) from Pedido p where p.fechaHora >= :inicio and p.fechaHora < :fin")
    long contarPedidosEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Pedidos de un cliente buscando por su email (JOIN con Cliente)
    @Query("select p from Pedido p join p.cliente c where lower(c.email) = lower(:email) order by p.fechaHora desc")
    List<Pedido> obtenerPedidosPorEmail(@Param("email") String email);

    // Suma de lo vendido entre dos fechas (función de agregado SUM)
    @Query("select coalesce(sum(p.base + p.iva), 0) from Pedido p where p.fechaHora >= :desde and p.fechaHora < :hasta")
    BigDecimal sumarVentasEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    // Resumen de ventas del día (endpoint 15)
    // Devuelve una sola fila: Object[] = {número de pedidos, total, base, iva}
    @Query("select count(p), coalesce(sum(p.base + p.iva), 0), coalesce(sum(p.base), 0), coalesce(sum(p.iva), 0) " +
            "from Pedido p where p.fechaHora >= :inicio and p.fechaHora < :fin")
    List<Object[]> obtenerVentasDia(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    //EVITAR N+1 (Q8): carga el pedido con sus líneas y el producto de cada línea en una sola consulta (endpoint 10)
    @EntityGraph(attributePaths = {"lineas", "lineas.producto"})
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> obtenerPedidoConLineas(@Param("id") Long id);

    //NATIVE QUERIES (Q3)

    // Ventas de un día agrupadas por hora (EXTRACT y DATE_TRUNC de PostgreSQL)
    // Cada fila es un Object[] = {hora, número de pedidos, total}
    @Query(value = "select cast(extract(hour from fecha_hora) as integer) as \"hora\", " +
            "count(*) as \"pedidos\", coalesce(sum(base + iva), 0) as \"total\" " +
            "from coffestation.pedido " +
            "where date_trunc('day', fecha_hora) = cast(:fecha as date) " +
            "group by 1 order by 1", nativeQuery = true)
    List<Object[]> obtenerVentasPorHora(@Param("fecha") LocalDate fecha);

    //EXTRA (nativa): clientes que más han gastado, para premiar a los mejores con cupones
    // Cada fila es un Object[] = {id del cliente, email, número de pedidos, total gastado}
    @Query(value = "select c.id as \"clienteId\", c.email as \"email\", count(p.id) as \"pedidos\", " +
            "coalesce(sum(p.base + p.iva), 0) as \"totalGastado\" " +
            "from coffestation.cliente c " +
            "join coffestation.pedido p on p.cliente_id = c.id " +
            "group by c.id, c.email " +
            "order by \"totalGastado\" desc " +
            "limit :limite", nativeQuery = true)
    List<Object[]> obtenerMejoresClientes(@Param("limite") int limite);

}
