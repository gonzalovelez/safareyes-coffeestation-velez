package es.safareyes.coffestation.service;

import es.safareyes.coffestation.Modelo.Pedido;
import es.safareyes.coffestation.Repositorio.Pedidorepo;
import es.safareyes.coffestation.dto.response.PedidoResponse;
import es.safareyes.coffestation.exception.RecursoNoEncontradoException;
import es.safareyes.coffestation.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

// lógica de los pedidos (de momento solo los GET)
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final Pedidorepo pedidorepo;
    private final PedidoMapper pedidoMapper;


    // endpoint 10 - GET /api/v1/pedidos/{id}
    // url: http://localhost:8080/api/v1/pedidos/10
    // TODO fase 4: un CLIENTE solo puede ver sus propios pedidos
    @Override
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Long id) {
        log.info("Detalle del pedido {}", id);

        // el @EntityGraph del repo ya trae cliente, líneas y productos de golpe
        Pedido pedido = pedidorepo.obtenerPedidoConLineas(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pedido con id " + id));

        return pedidoMapper.toResponse(pedido);
    }


    // endpoint 11 - GET /api/v1/pedidos
    // url: http://localhost:8080/api/v1/pedidos?fecha=2026-11-19&page=0&size=10
    // TODO: filtro por estado cuando la tabla pedido tenga la columna estado
    // TODO fase 4: un CLIENTE solo ve los suyos
    //
    // aquí el @Transactional(readOnly = true) hace falta de verdad: las líneas son LAZY
    // y el mapper las pide al convertir cada pedido, así que la conexión tiene que seguir abierta
    @Override
    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(LocalDate fecha, Pageable pageable) {
        log.info("Listado de pedidos: fecha={}, pagina={}", fecha, pageable);

        Page<Pedido> pedidos;
        if (fecha == null) {
            pedidos = pedidorepo.findAll(pageable);
        } else {
            // el día entero: desde las 00:00 hasta justo antes de las 00:00 del día siguiente
            LocalDateTime inicio = fecha.atStartOfDay();
            LocalDateTime fin = fecha.plusDays(1).atStartOfDay().minusNanos(1);
            pedidos = pedidorepo.findByFechaHoraBetween(inicio, fin, pageable);
        }

        return pedidos.map(pedidoMapper::toResponse);
    }
}
