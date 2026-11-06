package es.safareyes.coffestation.dto.response;

// GET /categorias: cada categoría con su número de productos activos
public record CategoriaResponse(
        Long id,
        String nombre,
        Boolean disponible,
        Long productosActivos
) {
}
