package es.safareyes.coffestation.service;

import es.safareyes.coffestation.dto.request.DisponibilidadRequest;
import es.safareyes.coffestation.dto.request.ProductoRequest;
import es.safareyes.coffestation.dto.response.ProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

// Operaciones de negocio sobre productos (endpoints 1 a 5).
// El controlador solo conoce esta interfaz; la lógica está en ProductoServiceImpl.
public interface ProductoService {

    // Endpoint 1: carta paginada. Todos los filtros son opcionales (null = no se filtra por ese campo).
    Page<ProductoResponse> listarCarta(Long categoriaId, String texto, BigDecimal precioMax,
                                       Boolean disponible, Long sinAlergenoId, Pageable pageable);

    // Endpoint 2: detalle de un producto activo con sus alérgenos
    ProductoResponse obtenerPorId(Long id);

    // Endpoint 3: alta de un producto
    ProductoResponse crear(ProductoRequest request);

    // Endpoint 4: modificación de un producto
    ProductoResponse actualizar(Long id, ProductoRequest request);

    // Endpoint 5: marcar un producto como disponible o agotado
    ProductoResponse cambiarDisponibilidad(Long id, DisponibilidadRequest request);
}
