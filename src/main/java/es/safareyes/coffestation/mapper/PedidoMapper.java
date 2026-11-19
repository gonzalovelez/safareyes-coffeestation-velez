package es.safareyes.coffestation.mapper;

import es.safareyes.coffestation.Modelo.Pedido;
import es.safareyes.coffestation.dto.response.PedidoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// pasa de Pedido (entidad) a su dto.
// uses = LineaPedidoMapper: para la lista de líneas MapStruct llama a ese mapper con cada línea
@Mapper(uses = LineaPedidoMapper.class)
public interface PedidoMapper {

    // el cliente se queda solo con su nombre.
    // total no está en la tabla, así que lo calculamos: base + iva
    @Mapping(target = "cliente", source = "cliente.nombre")
    @Mapping(target = "total", expression = "java(pedido.getBase().add(pedido.getIva()))")
    @Mapping(target = "lineas", source = "lineas")
    PedidoResponse toResponse(Pedido pedido);
}
