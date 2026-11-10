package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Alergeno;
import es.safareyes.coffestation.Modelo.Producto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// filtros de la carta (GET /api/v1/productos, RF-02). es la consulta Q7 (Specification) del enunciado.
//
// explicación para tontos:
// una Specification es un trozo de WHERE metido en un objeto. en vez de escribir una consulta
// para cada combinación de filtros (categoría + texto, texto + precio, ...), hacemos un trozo
// por filtro y luego pegamos solo los que el cliente ha mandado. si no manda ninguno,
// sale la carta entera.
//
// en cada lambda (root, query, cb):
//   root  -> el producto que estamos mirando (root.get("precio") = su precio)
//   query -> la consulta entera (la usamos para meter una subconsulta)
//   cb    -> el "constructor" de condiciones: equal (=), like, lessThanOrEqualTo (<=), not...
public final class ProductoSpecification {

    private ProductoSpecification() {
        // solo tiene métodos static, no hace falta crear objetos de esta clase
    }

    // RN-01: los productos dados de baja no salen nunca en la carta
    public static Specification<Producto> activo() {
        return (root, query, cb) -> cb.isTrue(root.get("activo"));
    }

    // where categoria_id = :categoriaId
    public static Specification<Producto> deCategoria(Long categoriaId) {
        return (root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    // where lower(nombre) like '%texto%'
    // se pasa todo a minúsculas para que "CAFÉ" y "café" encuentren lo mismo
    public static Specification<Producto> nombreContiene(String texto) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + texto.toLowerCase() + "%");
    }

    // where precio <= :precioMax
    public static Specification<Producto> precioMaximo(BigDecimal precioMax) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("precio"), precioMax);
    }

    // where disponible = :disponible  (true = hay, false = agotado)
    public static Specification<Producto> disponible(Boolean disponible) {
        return (root, query, cb) -> cb.equal(root.get("disponible"), disponible);
    }

    // productos que NO llevan un alérgeno.
    //
    // explicación para tontos:
    // no vale con "where alergeno <> 7", porque un producto con gluten y lácteos tiene dos filas
    // en alergeno_producto y la del gluten pasaría el filtro. así que lo hacemos en dos pasos:
    //   1. subconsulta: saco los ids de los productos que SÍ llevan ese alérgeno
    //   2. me quedo con los productos cuyo id NO está en esa lista
    // en sql sería:
    //   where id not in (select p.id from producto p join alergeno_producto ap ... where ap.alergeno_id = :id)
    public static Specification<Producto> sinAlergeno(Long alergenoId) {
        return (root, query, cb) -> {
            Subquery<Long> conAlergeno = query.subquery(Long.class);
            Root<Producto> producto = conAlergeno.from(Producto.class);
            Join<Producto, Alergeno> alergeno = producto.join("alergenos");
            conAlergeno.select(producto.get("id")).where(cb.equal(alergeno.get("id"), alergenoId));
            return cb.not(root.get("id").in(conAlergeno));
        };
    }

    // junta los filtros que han llegado (los que vienen a null se saltan).
    // activo() va siempre porque la carta nunca enseña productos de baja (RN-01).
    // allOf = los une todos con AND
    public static Specification<Producto> carta(Long categoriaId, String texto, BigDecimal precioMax,
                                                Boolean disponible, Long sinAlergenoId) {
        List<Specification<Producto>> filtros = new ArrayList<>();
        filtros.add(activo());
        if (categoriaId != null) {
            filtros.add(deCategoria(categoriaId));
        }
        if (texto != null && !texto.isBlank()) {
            filtros.add(nombreContiene(texto.trim()));
        }
        if (precioMax != null) {
            filtros.add(precioMaximo(precioMax));
        }
        if (disponible != null) {
            filtros.add(disponible(disponible));
        }
        if (sinAlergenoId != null) {
            filtros.add(sinAlergeno(sinAlergenoId));
        }
        return Specification.allOf(filtros);
    }
}
