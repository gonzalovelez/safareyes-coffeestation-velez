package es.safareyes.coffestation.service;

import es.safareyes.coffestation.dto.request.DisponibilidadRequest;
import es.safareyes.coffestation.dto.request.ProductoRequest;
import es.safareyes.coffestation.dto.response.ProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

// lo que se puede hacer con los productos (endpoints 1 a 5).
// el controlador solo ve esta interfaz; el código de verdad está en ProductoServiceImpl
public interface ProductoService {

    // endpoint 1: carta paginada. todos los filtros son opcionales (si llega null, no se filtra por eso)
    Page<ProductoResponse> listarCarta(Long categoriaId, String texto, BigDecimal precioMax,
                                       Boolean disponible, Long sinAlergenoId, Pageable pageable);

    // endpoint 2: detalle de un producto activo con sus alérgenos
    ProductoResponse obtenerPorId(Long id);

    // endpoint 3: dar de alta un producto
    ProductoResponse crear(ProductoRequest request);

    // endpoint 4: modificar un producto
    ProductoResponse actualizar(Long id, ProductoRequest request);

    // endpoint 5: marcar un producto como disponible o agotado
    ProductoResponse cambiarDisponibilidad(Long id, DisponibilidadRequest request);
}
