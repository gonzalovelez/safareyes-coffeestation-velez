package es.safareyes.coffestation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

// lo que llega al crear (POST) o modificar (PUT) un producto.
// el cliente manda ids (categoriaId, alergenosIds), no objetos: el servicio busca las entidades.
public record ProductoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripción no puede pasar de 255 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio tiene que ser mayor que 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite como mucho 2 decimales")
        BigDecimal precio,

        @NotNull(message = "Hay que indicar la categoría")
        Long categoriaId,

        // opcional: si no se manda, el producto no tiene alérgenos
        List<Long> alergenosIds
) {
}
