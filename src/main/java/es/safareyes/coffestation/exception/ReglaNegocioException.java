package es.safareyes.coffestation.exception;

// se lanza cuando la petición está bien escrita pero se salta una regla de negocio
// (p. ej. cambiar la disponibilidad de un producto que está dado de baja).
// GlobalExceptionHandler la pilla y la convierte en un 422.
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
