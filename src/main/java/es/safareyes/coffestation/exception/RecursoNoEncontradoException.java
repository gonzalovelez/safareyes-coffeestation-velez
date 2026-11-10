package es.safareyes.coffestation.exception;

// Se lanza cuando se pide algo que no existe (producto, categoría, alérgeno...).
// El GlobalExceptionHandler la convierte en un 404 Not Found.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
