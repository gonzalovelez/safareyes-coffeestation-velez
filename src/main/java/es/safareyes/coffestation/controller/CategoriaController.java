package es.safareyes.coffestation.controller;

import es.safareyes.coffestation.dto.response.CategoriaResponse;
import es.safareyes.coffestation.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// endpoints de las categorías. de momento solo el 7 (el 8, el PATCH, va luego)
//
// roles (fase 4):
//   GET -> público
@Slf4j
@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // 7. GET /api/v1/categorias
    // 200 con todas las categorías y su nº de productos activos (las que no tienen ninguno salen con 0)
    @GetMapping
    public List<CategoriaResponse> listar() {
        log.info("GET /categorias");
        return categoriaService.listar();
    }
}
