package es.safareyes.coffestation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// una línea dentro de un pedido: qué producto y cuántas unidades (RN-04: de 1 a 20)
public record LineaPedidoRequest(

        @NotNull(message = "Hay que indicar el producto")
        Long productoId,

        @NotNull(message = "Hay que indicar la cantidad")
        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 20, message = "La cantidad máxima es 20")
        Integer cantidad
) {
}
