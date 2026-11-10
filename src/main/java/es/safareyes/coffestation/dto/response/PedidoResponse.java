package es.safareyes.coffestation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// lo que devuelve la API de un pedido.
// cliente va aplanado (su nombre) y total no está en la tabla: se calcula (base + iva).
public record PedidoResponse(
        Long id,
        Long numeroTurno,
        LocalDateTime fechaHora,
        String cliente,
        BigDecimal base,
        BigDecimal iva,
        BigDecimal total,
        List<LineaPedidoResponse> lineas
) {
}
