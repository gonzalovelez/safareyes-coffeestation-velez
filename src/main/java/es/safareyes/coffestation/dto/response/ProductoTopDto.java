package es.safareyes.coffestation.dto.response;

// Una fila del informe GET /informes/productos-top
public record ProductoTopDto(
        String producto,
        Long unidadesVendidas
) {
}
