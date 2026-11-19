package es.safareyes.coffestation.mapper;

import es.safareyes.coffestation.Modelo.ProductoPedido;
import es.safareyes.coffestation.dto.response.LineaPedidoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// pasa una línea de pedido (ProductoPedido) a su dto.
// los datos del producto salen de dentro de la línea (campos anidados: producto.id, producto.nombre)
@Mapper
public interface LineaPedidoMapper {

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "producto", source = "producto.nombre")
    LineaPedidoResponse toResponse(ProductoPedido linea);
}
