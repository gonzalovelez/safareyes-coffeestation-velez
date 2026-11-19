package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface Pedidorepo extends JpaRepository<Pedido, Long> {

    // GET /api/v1/pedidos/{id} (endpoint 10)
    // detalle de un pedido con su cliente, sus líneas y sus productos en una sola consulta
    @EntityGraph(attributePaths = {"cliente", "lineas", "lineas.producto"})
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> obtenerPedidoConLineas(@Param("id") Long id);

    // GET /api/v1/pedidos?fecha= (endpoint 11)
    // listado paginado de pedidos entre dos fechas (?page=&size=&sort=).
    // aquí el @EntityGraph solo trae el cliente: si metiéramos también las líneas (una colección)
    // hibernate no podría paginar en la bd y se traería todos los pedidos a memoria
    @EntityGraph(attributePaths = {"cliente"})
    Page<Pedido> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    // GET /api/v1/pedidos sin fecha (endpoint 11)
    // todos los pedidos paginados. lo repetimos solo para ponerle el @EntityGraph del cliente
    @Override
    @EntityGraph(attributePaths = {"cliente"})
    Page<Pedido> findAll(Pageable pageable);

    // GET /api/v1/informes/ventas-dia?fecha=
    // ventas entre dos momentos (devuelve una sola fila)
    @Query("select count(p), sum(p.base + p.iva), sum(p.base), sum(p.iva) " +
            "from Pedido p where p.fechaHora between :inicio and :fin")
    // Object[]: [0] = nº de pedidos, [1] = total, [2] = base, [3] = iva
    List<Object[]> obtenerVentasDia(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
