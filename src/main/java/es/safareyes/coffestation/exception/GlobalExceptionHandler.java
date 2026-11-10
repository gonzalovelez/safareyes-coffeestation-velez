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

// aquí se convierten todas las excepciones en respuestas http con su código de error.
//
// cuando algo peta en un controlador o en un servicio, la excepción "sube" hasta Spring.
// @RestControllerAdvice hace que Spring mire en esta clase si hay un método @ExceptionHandler
// para ese tipo de excepción, y lo que devuelva ese método es lo que le llega al cliente.
// así los servicios solo tienen que hacer "throw new RecursoNoEncontradoException(...)"
// y no se preocupan de códigos http.
//
// todos los errores salen con el formato ProblemDetail (estándar RFC 9457):
// { "type", "title", "status", "detail", "instance", ...y lo que queramos añadir }
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: lo que se busca no existe
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("404 - {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Recurso no encontrado");
        return problema;
    }

    // 422: la petición está bien escrita pero se salta una regla de negocio
    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail manejarReglaNegocio(ReglaNegocioException ex) {
        log.warn("422 - {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problema.setTitle("Regla de negocio incumplida");
        return problema;
    }

    // 400: no pasa las validaciones del DTO (@NotBlank, @DecimalMin...).
    // en "errores" va cada campo que falla con su mensaje, p. ej. { "nombre": "El nombre es obligatorio" }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        // putIfAbsent: si un campo falla dos validaciones, nos quedamos solo con el primer mensaje
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        log.warn("400 - Datos no válidos: {}", errores);

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "La petición tiene datos no válidos");
        problema.setTitle("Datos no válidos");
        problema.setProperty("errores", errores);
        return problema;
    }

    // 400: el cuerpo no es un json válido o un campo viene con un tipo raro ("precio": "abc")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail manejarJsonIlegible(HttpMessageNotReadableException ex) {
        log.warn("400 - JSON ilegible: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es un JSON válido o algún campo tiene un tipo incorrecto");
        problema.setTitle("Petición mal formada");
        return problema;
    }

    // 400: un parámetro de la url no es del tipo que toca (/productos/abc o ?precioMax=caro)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail manejarTipoParametro(MethodArgumentTypeMismatchException ex) {
        log.warn("400 - Parámetro '{}' con valor no válido: {}", ex.getName(), ex.getValue());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor no válido: " + ex.getValue());
        problema.setTitle("Parámetro no válido");
        return problema;
    }

    // 400: se intenta ordenar por un campo que no existe (?sort=noexiste)
    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail manejarOrdenNoValido(PropertyReferenceException ex) {
        log.warn("400 - Orden no válido: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "No se puede ordenar por '" + ex.getPropertyName() + "'");
        problema.setTitle("Parámetro no válido");
        return problema;
    }

    // 500: cualquier otro error que no hayamos previsto.
    // en el log se guarda entero, pero al cliente no le enseñamos la traza
    // (le daríamos pistas de cómo está hecho el programa por dentro).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> manejarErrorInesperado(Exception ex) {
        // ojo: este método lo pilla todo, también los errores normales de Spring
        // (405 método no permitido, 404 ruta que no existe, 415...). esos ya traen su código
        // y su ProblemDetail, así que los devolvemos tal cual en vez de convertirlos en un 500
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
