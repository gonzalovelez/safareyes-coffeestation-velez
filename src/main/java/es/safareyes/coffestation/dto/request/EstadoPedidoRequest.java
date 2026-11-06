package es.safareyes.coffestation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Cuerpo de PATCH /pedidos/{id}/estado
// Ejemplo: { "estado": "EN_PREPARACION" }
public record EstadoPedidoRequest(

        @NotBlank(message = "Hay que indicar el nuevo estado")
        @Pattern(regexp = "PENDIENTE|EN_PREPARACION|LISTO|ENTREGADO|CANCELADO",
                message = "Estado no válido: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO o CANCELADO")
        String estado
) {
}
