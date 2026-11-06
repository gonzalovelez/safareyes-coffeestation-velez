package es.safareyes.coffestation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Lo que llega al crear un cupón (POST /cupones, solo ADMIN)
public record CuponRequest(

        @NotBlank(message = "El código es obligatorio")
        @Size(max = 20, message = "El código no puede pasar de 20 caracteres")
        String codigo,

        @NotBlank(message = "El tipo es obligatorio")
        @Pattern(regexp = "PORCENTAJE|IMPORTE_FIJO", message = "El tipo tiene que ser PORCENTAJE o IMPORTE_FIJO")
        String tipo,

        @NotNull(message = "El valor es obligatorio")
        @DecimalMin(value = "0.01", message = "El valor tiene que ser mayor que 0")
        BigDecimal valor,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDateTime fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDateTime fechaFin,

        @NotNull(message = "El número máximo de usos es obligatorio")
        @Min(value = 1, message = "El cupón tiene que poder usarse al menos una vez")
        Long maxUsos,

        // Opcional: si no se manda, el cupón vale para cualquier importe
        @DecimalMin(value = "0.00", message = "El importe mínimo no puede ser negativo")
        BigDecimal importeMinimo,

        // Opcional: si se indica, el cupón es personal de ese cliente
        Long clienteId
) {
}
