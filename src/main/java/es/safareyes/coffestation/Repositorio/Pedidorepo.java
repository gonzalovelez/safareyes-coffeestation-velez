package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface Pedidorepo extends JpaRepository<Pedido, Long> {

    //JPQL (Q2)

    // Pedidos de un cliente buscando por su email (JOIN con Cliente)
    @Query("select p from Pedido p join p.cliente c where c.email = :email")
    List<Pedido> obtenerPedidosPorEmail(@Param("email") String email);

    // Total vendido entre dos fechas (SUM)
    @Query("select sum(p.base + p.iva) from Pedido p where p.fechaHora between :desde and :hasta")
    BigDecimal sumarVentasEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    //EVITAR N+1 (Q8): carga el pedido con sus líneas y sus productos en una sola consulta
    @EntityGraph(attributePaths = {"lineas", "lineas.producto"})
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> obtenerPedidoConLineas(@Param("id") Long id);

}
