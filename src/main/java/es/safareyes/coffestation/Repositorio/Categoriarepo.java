package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface Categoriarepo extends JpaRepository<Categoria, Long> {

    // GET /api/v1/categorias
    // Categorías con su número de productos activos. Cada fila es un Object[] = {nombre, nº de productos}
    @Query("select c.nombre, count(p) from Categoria c left join c.productos p on p.activo = true " +
            "group by c.nombre order by c.nombre")
    List<Object[]> obtenerCategoriasConProductos();

}
