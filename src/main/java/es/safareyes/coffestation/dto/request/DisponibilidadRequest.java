package es.safareyes.coffestation.dto.request;

import jakarta.validation.constraints.NotNull;

// Cuerpo de PATCH /productos/{id}/disponibilidad y de PATCH /categorias/{id}/disponibilidad
// Ejemplo: { "disponible": false }  -> marcar como agotado
public record DisponibilidadRequest(

        @NotNull(message = "Hay que indicar si está disponible (true o false)")
        Boolean disponible
) {
}
