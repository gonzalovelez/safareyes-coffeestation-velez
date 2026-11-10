package es.safareyes.coffestation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

// informe GET /informes/ventas-dia?fecha=
public record VentasDiaDto(
        LocalDate fecha,
        Long numeroPedidos,
        BigDecimal total,
        BigDecimal base,
        BigDecimal iva
) {
}
