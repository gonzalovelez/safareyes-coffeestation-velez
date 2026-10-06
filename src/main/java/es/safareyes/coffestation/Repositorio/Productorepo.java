package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository

public interface Productorepo extends JpaRepository<Producto, Long> {

    //USANDO JPA INTERFACE (Q1)

    // Productos activos de una categoría
    List<Producto> findByCategoria_IdAndActivoTrue(Long categoriaId);

    // Productos cuyo nombre contiene un texto, sin distinguir mayúsculas
    List<Producto> findByNombreContainingIgnoreCase(String texto);

    //PAGINACIÓN (Q6): la carta paginada con ?page=&size=&sort=
    Page<Producto> findByActivoTrue(Pageable pageable);

    //MODIFICACIÓN MASIVA (Q4): agotar o reponer todos los productos de una categoría
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Producto p set p.disponible = :disponible where p.categoria.id = :categoriaId")
    int cambiarDisponibilidadCategoria(@Param("categoriaId") Long categoriaId,
                                       @Param("disponible") Boolean disponible);

    //NATIVE QUERIES (Q3)

    // Productos más vendidos. Cada fila es un Object[] = {nombre, unidades vendidas}
    @Query(value = "select pr.nombre, sum(pp.cantidad) from coffestation.producto_pedido pp " +
            "join coffestation.producto pr on pr.id = pp.producto_id " +
            "group by pr.nombre order by sum(pp.cantidad) desc limit :limite", nativeQuery = true)
    List<Object[]> obtenerProductosMasVendidos(@Param("limite") int limite);

}
