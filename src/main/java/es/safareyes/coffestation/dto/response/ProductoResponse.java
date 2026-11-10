package es.safareyes.coffestation.dto.response;

import java.math.BigDecimal;
import java.util.List;

// lo que devuelve la API de un producto (carta y detalle).
// aplanado: la categoría y los alérgenos van por su nombre, no como objetos.
public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Boolean disponible,
        String categoria,
        List<String> alergenos
) {
}
