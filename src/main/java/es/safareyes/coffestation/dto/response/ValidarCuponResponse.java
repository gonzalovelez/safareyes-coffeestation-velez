package es.safareyes.coffestation.dto.response;

import java.math.BigDecimal;

// respuesta de POST /cupones/validar
// ejemplo: { "valido": true, "mensaje": "Cupón válido", "descuento": 1.50 }
public record ValidarCuponResponse(
        boolean valido,
        String mensaje,
        BigDecimal descuento
) {
}
