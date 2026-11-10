package es.safareyes.coffestation.dto.response;

// una línea de un pedido tal como la ve el cliente
public record LineaPedidoResponse(
        Long productoId,
        String producto,
        Long cantidad
) {
}
