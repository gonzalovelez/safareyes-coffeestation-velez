package es.safareyes.coffestation.dto.request;

import jakarta.validation.constraints.NotNull;

// cuerpo de PATCH /productos/{id}/disponibilidad y de PATCH /categorias/{id}/disponibilidad
// ejemplo: { "disponible": false }  -> marcar como agotado
public record DisponibilidadRequest(

        @NotNull(message = "Hay que indicar si está disponible (true o false)")
        Boolean disponible
) {
}
