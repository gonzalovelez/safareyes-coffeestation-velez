package es.safareyes.coffestation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// cuerpo de POST /cupones/validar: ¿se puede usar este cupón con este importe?
// ejemplo: { "codigo": "BIENVENIDA10", "subtotal": 12.50 }
public record ValidarCuponRequest(

        @NotBlank(message = "El código del cupón es obligatorio")
        String codigo,

        @NotNull(message = "El subtotal es obligatorio")
        @DecimalMin(value = "0.00", message = "El subtotal no puede ser negativo")
        BigDecimal subtotal
) {
}
