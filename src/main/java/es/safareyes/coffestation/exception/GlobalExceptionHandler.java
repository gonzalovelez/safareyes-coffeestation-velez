package es.safareyes.coffestation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

// Único punto donde se traducen las excepciones a respuestas HTTP.
// Todas las respuestas de error usan ProblemDetail (RFC 9457):
// { "type", "title", "status", "detail", "instance", ...propiedades extra }
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: el recurso pedido no existe
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("404 - {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Recurso no encontrado");
        return problema;
    }

    // 422: la petición es correcta pero incumple una regla de negocio
    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail manejarReglaNegocio(ReglaNegocioException ex) {
        log.warn("422 - {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problema.setTitle("Regla de negocio incumplida");
        return problema;
    }

    // 400: falla alguna validación de un DTO (@NotBlank, @DecimalMin...).
    // Se devuelve qué campo falla y por qué en la propiedad "errores".
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        log.warn("400 - Datos no válidos: {}", errores);

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "La petición tiene datos no válidos");
        problema.setTitle("Datos no válidos");
        problema.setProperty("errores", errores);
        return problema;
    }

    // 400: el cuerpo no es un JSON válido o un campo tiene un tipo incorrecto ("precio": "abc")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail manejarJsonIlegible(HttpMessageNotReadableException ex) {
        log.warn("400 - JSON ilegible: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es un JSON válido o algún campo tiene un tipo incorrecto");
        problema.setTitle("Petición mal formada");
        return problema;
    }

    // 400: un parámetro de la URL tiene un tipo incorrecto (/productos/abc o ?precioMax=caro)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail manejarTipoParametro(MethodArgumentTypeMismatchException ex) {
        log.warn("400 - Parámetro '{}' con valor no válido: {}", ex.getName(), ex.getValue());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor no válido: " + ex.getValue());
        problema.setTitle("Parámetro no válido");
        return problema;
    }

    // 400: se pide ordenar por un campo que no existe (?sort=noexiste)
    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail manejarOrdenNoValido(PropertyReferenceException ex) {
        log.warn("400 - Orden no válido: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "No se puede ordenar por '" + ex.getPropertyName() + "'");
        problema.setTitle("Parámetro no válido");
        return problema;
    }

    // 500: cualquier error no previsto. Se registra completo en el log,
    // pero al cliente no se le enseña la traza (podría revelar detalles internos).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> manejarErrorInesperado(Exception ex) {
        // Las excepciones propias de Spring MVC (405 método no permitido, 404 ruta inexistente, 415...)
        // ya traen su código y su ProblemDetail: se devuelven tal cual en vez de convertirlas en 500
        if (ex instanceof ErrorResponse errorSpring) {
            return ResponseEntity.status(errorSpring.getStatusCode()).body(errorSpring.getBody());
        }
        log.error("500 - Error inesperado", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Se ha producido un error interno");
        problema.setTitle("Error interno");
        return ResponseEntity.internalServerError().body(problema);
    }
}
