package es.safareyes.coffestation.exception;

// se lanza cuando se busca algo que no existe (producto, categoría, alérgeno...).
// GlobalExceptionHandler la pilla y la convierte en un 404.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
