package es.safareyes.coffestation.dto.response;

import java.math.BigDecimal;

// Respuesta de POST /cupones/validar
// Ejemplo: { "valido": true, "mensaje": "Cupón válido", "descuento": 1.50 }
public record ValidarCuponResponse(
        boolean valido,
        String mensaje,
        BigDecimal descuento
) {
}
