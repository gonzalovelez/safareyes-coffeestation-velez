package es.safareyes.coffestation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

// Lo que llega al crear un pedido (POST /pedidos).
// codigoCupon y dniSocio son opcionales (RN-07: se aplica el mayor descuento, no los dos).
public record PedidoRequest(

        // Opcional: un pedido de barra puede no tener cliente
        Long clienteId,

        // RN-04: de 1 a 15 líneas. @Valid hace que también se valide cada línea por dentro
        @NotEmpty(message = "El pedido tiene que tener al menos una línea")
        @Size(max = 15, message = "Un pedido no puede tener más de 15 líneas")
        List<@Valid LineaPedidoRequest> lineas,

        @Size(max = 20, message = "El código de cupón no puede pasar de 20 caracteres")
        String codigoCupon,

        @Size(min = 9, max = 9, message = "El DNI tiene que tener 9 caracteres (8 números y una letra)")
        String dniSocio
) {
}
