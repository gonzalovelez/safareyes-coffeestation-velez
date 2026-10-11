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

    // GET /api/v1/pedidos/{id}
    // Detalle de un pedido con sus líneas y sus productos en una sola consulta
    @EntityGraph(attributePaths = {"lineas", "lineas.producto"})
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> obtenerPedidoConLineas(@Param("id") Long id);

    // GET /api/v1/pedidos
    // Listado paginado de pedidos entre dos fechas (?page=&size=&sort=)
    Page<Pedido> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    // GET /api/v1/informes/ventas-dia?fecha=
    // Ventas entre dos momentos. Devuelve una fila: Object[] = {nº de pedidos, total, base, iva}
    @Query("select count(p), sum(p.base + p.iva), sum(p.base), sum(p.iva) " +
            "from Pedido p where p.fechaHora between :inicio and :fin")
    List<Object[]> obtenerVentasDia(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
