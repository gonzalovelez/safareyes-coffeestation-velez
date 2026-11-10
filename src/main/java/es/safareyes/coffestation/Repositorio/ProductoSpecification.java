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

// Filtros combinables de la carta (GET /api/v1/productos, RF-02).
// Cada método devuelve un trozo de WHERE; el método carta() junta solo los filtros que llegan
// (los que vienen a null no se aplican). Es la consulta Q7 (Specification) del enunciado.
public final class ProductoSpecification {

    private ProductoSpecification() {
        // Clase de utilidades: no se instancia
    }

    // RN-01: un producto dado de baja nunca sale en la carta
    public static Specification<Producto> activo() {
        return (root, query, cb) -> cb.isTrue(root.get("activo"));
    }

    // WHERE categoria_id = :categoriaId
    public static Specification<Producto> deCategoria(Long categoriaId) {
        return (root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    // WHERE lower(nombre) LIKE '%texto%'  (no distingue mayúsculas)
    public static Specification<Producto> nombreContiene(String texto) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + texto.toLowerCase() + "%");
    }

    // WHERE precio <= :precioMax
    public static Specification<Producto> precioMaximo(BigDecimal precioMax) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("precio"), precioMax);
    }

    // WHERE disponible = :disponible  (true = hay existencias, false = agotado)
    public static Specification<Producto> disponible(Boolean disponible) {
        return (root, query, cb) -> cb.equal(root.get("disponible"), disponible);
    }

    // Productos que NO contienen un alérgeno:
    // WHERE id NOT IN (SELECT p.id FROM producto p JOIN alergeno_producto ... WHERE alergeno_id = :alergenoId)
    public static Specification<Producto> sinAlergeno(Long alergenoId) {
        return (root, query, cb) -> {
            Subquery<Long> conAlergeno = query.subquery(Long.class);
            Root<Producto> producto = conAlergeno.from(Producto.class);
            Join<Producto, Alergeno> alergeno = producto.join("alergenos");
            conAlergeno.select(producto.get("id")).where(cb.equal(alergeno.get("id"), alergenoId));
            return cb.not(root.get("id").in(conAlergeno));
        };
    }

    // Junta todos los filtros que no vengan a null. El filtro activo() va siempre (RN-01).
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
