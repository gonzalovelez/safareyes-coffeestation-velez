package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

// JpaSpecificationExecutor nos regala el findAll(Specification, Pageable) que usa la carta con filtros
public interface Productorepo extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    // GET /api/v1/productos (endpoint 1)
    // carta paginada con los filtros de ProductoSpecification.
    //
    // explicación para tontos:
    // este método ya existe (viene de JpaSpecificationExecutor), lo repetimos solo para ponerle
    // el @EntityGraph. sin él, al sacar 10 productos Hibernate haría 1 consulta para los productos
    // y luego 1 más por cada producto para buscar su categoría (el famoso problema N+1).
    // con @EntityGraph se trae la categoría en el mismo select con un join.
    @Override
    @EntityGraph(attributePaths = {"categoria"})
    Page<Producto> findAll(Specification<Producto> spec, Pageable pageable);

    // GET /api/v1/productos
    // carta paginada: productos activos cuyo nombre contiene un texto (?page=&size=&sort=)
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String texto, Pageable pageable);

    // GET /api/v1/productos/{id} (endpoint 2)
    // detalle de un producto con sus alérgenos y su categoría, todo en una sola consulta
    @EntityGraph(attributePaths = {"alergenos", "categoria"})
    Optional<Producto> findWithAlergenosById(Long id);

    // GET /api/v1/informes/productos-top?desde=&hasta=&limite=
    // productos más vendidos entre dos fechas
    @Query(value = "select pr.nombre, sum(pp.cantidad) from coffestation.producto_pedido pp " +
            "join coffestation.producto pr on pr.id = pp.producto_id " +
            "join coffestation.pedido pe on pe.id = pp.pedido_id " +
            "where pe.fecha_hora between :desde and :hasta " +
            "group by pr.nombre order by sum(pp.cantidad) desc limit :limite", nativeQuery = true)
    // Object[]: [0] = nombre del producto, [1] = unidades vendidas
    List<Object[]> obtenerProductosTop(@Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta,
                                       @Param("limite") int limite);

    // EXTRA: productos agotados
    List<Producto> findByDisponibleFalse();

}
