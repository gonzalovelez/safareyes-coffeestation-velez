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

// Endpoints 1 a 5 de la carta. El controlador solo recibe y devuelve DTOs:
// no usa repositorios ni entidades, toda la lógica está en ProductoService.
//
// Roles (se aplicarán en la fase 4 con Spring Security; ahora todo está abierto):
//   GET           -> público
//   POST, PUT     -> ADMIN
//   PATCH         -> ADMIN, BARISTA
@Slf4j
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // 1. GET /api/v1/productos?categoriaId=&texto=&precioMax=&disponible=&sinAlergenoId=&page=&size=&sort=
    // Carta paginada (por defecto 10 por página, ordenada por nombre). Todos los filtros son opcionales.
    // 200 OK con la página de productos.
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
    // 200 OK con el producto y sus alérgenos; 404 si no existe o está dado de baja.
    @GetMapping("/{id}")
    public ProductoResponse obtenerPorId(@PathVariable Long id) {
        log.info("GET /productos/{}", id);
        return productoService.obtenerPorId(id);
    }

    // 3. POST /api/v1/productos  (ADMIN)
    // 201 Created + cabecera Location con la URL del producto nuevo;
    // 400 si los datos no son válidos; 404 si la categoría o algún alérgeno no existe.
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        log.info("POST /productos");
        ProductoResponse creado = productoService.crear(request);

        // Location: http://localhost:8080/api/v1/productos/{id del nuevo}
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    // 4. PUT /api/v1/productos/{id}  (ADMIN)
    // 200 OK con el producto modificado; 400 datos no válidos; 404 no existe; 422 dado de baja.
    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        log.info("PUT /productos/{}", id);
        return productoService.actualizar(id, request);
    }

    // 5. PATCH /api/v1/productos/{id}/disponibilidad  (ADMIN, BARISTA)
    // Cuerpo: { "disponible": false } para marcarlo agotado, true para reponerlo.
    // 200 OK con el producto; 400 si falta "disponible"; 404 no existe; 422 dado de baja.
    @PatchMapping("/{id}/disponibilidad")
    public ProductoResponse cambiarDisponibilidad(@PathVariable Long id,
                                                  @Valid @RequestBody DisponibilidadRequest request) {
        log.info("PATCH /productos/{}/disponibilidad", id);
        return productoService.cambiarDisponibilidad(id, request);
    }
}
