package es.safareyes.coffestation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Lo que devuelve la API de un cupón.
// emailCliente solo viene relleno si el cupón es personal.
public record CuponResponse(
        Long id,
        String codigo,
        String tipo,
        BigDecimal valor,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Long maxUsos,
        BigDecimal importeMinimo,
        String emailCliente
) {
}
