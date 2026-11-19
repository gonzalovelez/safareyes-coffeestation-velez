package es.safareyes.coffestation.service;

import es.safareyes.coffestation.dto.response.PedidoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

// lo que se puede hacer con los pedidos (de momento los GET: endpoints 10 y 11)
public interface PedidoService {

    // endpoint 10: detalle de un pedido con sus líneas
    PedidoResponse obtenerPorId(Long id);

    // endpoint 11: pedidos paginados; si llega fecha, solo los de ese día
    Page<PedidoResponse> listar(LocalDate fecha, Pageable pageable);
}
