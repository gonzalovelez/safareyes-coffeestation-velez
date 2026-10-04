package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface Categoriarepo extends JpaRepository<Categoria, Long> {

    //JPQL (Q2 con COUNT + GROUP BY)
    // Categorías con su número de productos activos (endpoint 7).
    // LEFT JOIN para que también salgan las categorías sin productos (con 0)
    // Cada fila es un Object[] = {id, nombre, número de productos activos}
    @Query("select c.id, c.nombre, count(p) " +
            "from Categoria c left join c.productos p on p.activo = true " +
            "group by c.id, c.nombre order by c.nombre")
    List<Object[]> obtenerCategoriasConProductosActivos();

}
