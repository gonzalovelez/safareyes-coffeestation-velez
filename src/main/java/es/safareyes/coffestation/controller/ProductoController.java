package es.safareyes.coffestation.controller;

import es.safareyes.coffestation.dto.request.DisponibilidadRequest;
import es.safareyes.coffestation.dto.request.ProductoRequest;
import es.safareyes.coffestation.dto.response.ProductoResponse;
import es.safareyes.coffestation.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

// endpoints 1 a 5 de la carta. el controlador solo recibe y devuelve DTOs:
// ni toca repositorios ni devuelve entidades, todo el trabajo lo hace ProductoService.
//
// roles (se pondrán en la fase 4 con Spring Security; de momento está todo abierto):
//   GET        -> público
//   POST, PUT  -> ADMIN
//   PATCH      -> ADMIN, BARISTA
@Slf4j
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // 1. GET /api/v1/productos?categoriaId=&texto=&precioMax=&disponible=&sinAlergenoId=&page=&size=&sort=
    // carta paginada (10 por página y ordenada por nombre si no se dice otra cosa).
    // todos los filtros son opcionales. devuelve 200 con la página.
    //
    // explicación para tontos del Pageable:
    // Spring lee solo los parámetros page (qué página, empieza en 0), size (cuántos por página)
    // y sort (por qué campo ordenar, p. ej. sort=precio,desc) y nos los da metidos en el objeto
    // Pageable. @PageableDefault pone los valores por defecto si no llegan.
    @GetMapping
    public Page<ProductoResponse> listarCarta(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) Long sinAlergenoId,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        log.info("GET /productos");
        return productoService.listarCarta(categoriaId, texto, precioMax, disponible, sinAlergenoId, pageable);
    }

    // 2. GET /api/v1/productos/{id}
    // 200 con el producto y sus alérgenos; 404 si no existe o está de baja
    @GetMapping("/{id}")
    public ProductoResponse obtenerPorId(@PathVariable Long id) {
        log.info("GET /productos/{}", id);
        return productoService.obtenerPorId(id);
    }

    // 3. POST /api/v1/productos  (ADMIN)
    // 201 + cabecera Location con la url del producto nuevo;
    // 400 si los datos están mal; 404 si la categoría o algún alérgeno no existe.
    // @Valid = antes de entrar aquí, Spring comprueba las validaciones del ProductoRequest
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        log.info("POST /productos");
        ProductoResponse creado = productoService.crear(request);

        // montamos la url del producto nuevo cogiendo la de esta petición y añadiendo el id:
        // http://localhost:8080/api/v1/productos  ->  http://localhost:8080/api/v1/productos/21
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    // 4. PUT /api/v1/productos/{id}  (ADMIN)
    // 200 con el producto cambiado; 400 datos mal; 404 no existe; 422 está de baja
    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        log.info("PUT /productos/{}", id);
        return productoService.actualizar(id, request);
    }

    // 5. PATCH /api/v1/productos/{id}/disponibilidad  (ADMIN, BARISTA)
    // cuerpo: { "disponible": false } para marcarlo agotado, true para reponerlo.
    // 200 con el producto; 400 si falta "disponible"; 404 no existe; 422 está de baja
    @PatchMapping("/{id}/disponibilidad")
    public ProductoResponse cambiarDisponibilidad(@PathVariable Long id,
                                                  @Valid @RequestBody DisponibilidadRequest request) {
        log.info("PATCH /productos/{}/disponibilidad", id);
        return productoService.cambiarDisponibilidad(id, request);
    }
}
