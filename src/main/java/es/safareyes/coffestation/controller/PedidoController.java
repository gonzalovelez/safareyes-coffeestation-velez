package es.safareyes.coffestation.controller;

import es.safareyes.coffestation.dto.response.PedidoResponse;
import es.safareyes.coffestation.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// endpoints de los pedidos. de momento solo los GET (10 y 11); el POST (9) y el PATCH (12) van luego
//
// roles (fase 4):
//   GET -> ADMIN, BARISTA y CLIENTE (solo los suyos)
@Slf4j
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // 10. GET /api/v1/pedidos/{id}
    // 200 con el pedido y sus líneas; 404 si no existe; 400 si el id no es un número
    @GetMapping("/{id}")
    public PedidoResponse obtenerPorId(@PathVariable Long id) {
        log.info("GET /pedidos/{}", id);
        return pedidoService.obtenerPorId(id);
    }

    // 11. GET /api/v1/pedidos?fecha=2026-11-19&page=0&size=10&sort=fechaHora,desc
    // pedidos paginados (10 por página, los más nuevos primero si no se dice otra cosa).
    // fecha es opcional, en formato yyyy-MM-dd; si no llega, salen todos.
    // 400 si la fecha está mal escrita (?fecha=ayer)
    @GetMapping
    public Page<PedidoResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @PageableDefault(size = 10, sort = "fechaHora", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /pedidos");
        return pedidoService.listar(fecha, pageable);
    }
}
