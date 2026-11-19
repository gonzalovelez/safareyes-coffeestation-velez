package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Categoria;
import es.safareyes.coffestation.dto.response.CategoriaResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface Categoriarepo extends JpaRepository<Categoria, Long> {

    // GET /api/v1/categorias (endpoint 7)
    // categorías con su número de productos activos.
    // con "select new" hibernate crea directamente el dto, sin pasar por Object[] (proyección Q5).
    // el left join hace que salgan también las categorías sin ningún producto activo (con 0)
    @Query("select new es.safareyes.coffestation.dto.response.CategoriaResponse(" +
            "c.id, c.nombre, c.disponible, count(p)) " +
            "from Categoria c left join c.productos p on p.activo = true " +
            "group by c.id, c.nombre, c.disponible order by c.nombre")
    List<CategoriaResponse> obtenerCategoriasConProductos();

}
