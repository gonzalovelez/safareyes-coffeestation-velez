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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface Productorepo extends JpaRepository<Producto, Long> {

    //USANDO JPA INTERFACE (Q1: métodos derivados del nombre)

    // Productos activos de una categoría. "Categoria_Id" navega a la relación Categoria y usa su Id
    List<Producto> findByCategoria_IdAndActivoTrue(Long categoriaId);

    // Búsqueda por texto en el nombre o en la descripción, sin distinguir mayúsculas (Or + Containing + IgnoreCase)
    List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);

    // Productos disponibles en un rango de precio, del más barato al más caro (Between + OrderBy)
    List<Producto> findByPrecioBetweenAndDisponibleTrueOrderByPrecioAsc(BigDecimal minimo, BigDecimal maximo);

    // Número de productos activos de una categoría (countBy)
    long countByCategoria_IdAndActivoTrue(Long categoriaId);

    //PAGINACIÓN (Q6): Pageable recibe ?page=&size=&sort= y Page devuelve la página y el total
    Page<Producto> findByActivoTrue(Pageable pageable);

    //JPQL (Q2)

    // Productos activos que NO contienen un alérgeno (JOIN + NOT IN)
    @Query("select p from Producto p where p.activo = true and p.id not in " +
            "(select p2.id from Producto p2 join p2.alergenos a where a.id = :alergenoId) " +
            "order by p.nombre")
    List<Producto> obtenerProductosSinAlergeno(@Param("alergenoId") Long alergenoId);

    //JPQL: la carta solo con los datos que se muestran
    // Cada fila es un Object[] = {id, nombre, precio, disponible, nombre de la categoría}
    @Query("select p.id, p.nombre, p.precio, p.disponible, c.nombre " +
            "from Producto p join p.categoria c " +
            "where p.activo = true order by c.nombre, p.nombre")
    List<Object[]> obtenerCarta();

    //MODIFICACIÓN MASIVA (Q4): agotar o reponer todos los productos de una categoría (RF-03)
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Producto p set p.disponible = :disponible where p.categoria.id = :categoriaId")
    int cambiarDisponibilidadCategoria(@Param("categoriaId") Long categoriaId,
                                       @Param("disponible") Boolean disponible);

    //NATIVE QUERIES (Q3)

    // Ranking de productos más vendidos entre dos fechas (endpoint 16)
    // Cada fila es un Object[] = {id del producto, nombre, unidades, importe}
    @Query(value = "select pr.id as \"productoId\", pr.nombre as \"nombre\", " +
            "cast(sum(pp.cantidad) as bigint) as \"unidades\", " +
            "coalesce(sum(pp.cantidad * pr.precio), 0) as \"importe\" " +
            "from coffestation.producto_pedido pp " +
            "join coffestation.producto pr on pr.id = pp.producto_id " +
            "join coffestation.pedido pe on pe.id = pp.pedido_id " +
            "where date_trunc('day', pe.fecha_hora) between :desde and :hasta " +
            "group by pr.id, pr.nombre " +
            "order by \"unidades\" desc " +
            "limit :limite", nativeQuery = true)
    List<Object[]> obtenerProductosTop(@Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta,
                                       @Param("limite") int limite);

    //EXTRA (JPQL): productos activos que no se han vendido nunca, para revisar la carta
    @Query("select p from Producto p where p.activo = true and p.lineas is empty order by p.nombre")
    List<Producto> obtenerProductosNuncaVendidos();

}
