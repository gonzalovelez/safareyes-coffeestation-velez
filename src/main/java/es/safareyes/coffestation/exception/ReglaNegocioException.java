package es.safareyes.coffestation.exception;

// Se lanza cuando la petición está bien formada pero incumple una regla de negocio
// (p. ej. cambiar la disponibilidad de un producto dado de baja).
// El GlobalExceptionHandler la convierte en un 422 Unprocessable Entity.
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
