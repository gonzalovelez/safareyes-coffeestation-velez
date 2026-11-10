package es.safareyes.coffestation.mapper;

import es.safareyes.coffestation.Modelo.Alergeno;
import es.safareyes.coffestation.Modelo.Producto;
import es.safareyes.coffestation.dto.request.ProductoRequest;
import es.safareyes.coffestation.dto.response.ProductoResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

// Convierte Producto (entidad) <-> DTOs. MapStruct genera la implementación al compilar
// (target/generated-sources/annotations/.../ProductoMapperImpl.java) y la registra como bean de Spring.
//
// La categoría y los alérgenos NO se mapean desde el request: el request trae ids (categoriaId,
// alergenosIds) y es el servicio quien busca esas entidades en la BD.
@Mapper
public interface ProductoMapper {

    // Entidad -> respuesta. Campos anidados: la categoría se aplana a su nombre
    // y la colección de alérgenos se convierte en una lista de nombres (usa alergenoANombre).
    @Mapping(target = "categoria", source = "categoria.nombre")
    @Mapping(target = "alergenos", source = "alergenos")
    ProductoResponse toResponse(Producto producto);

    // Request -> entidad nueva (POST). Lo que no viene en el request lo rellena el servicio.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "disponible", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "alergenos", ignore = true)
    @Mapping(target = "lineas", ignore = true)
    Producto toEntity(ProductoRequest request);

    // Request -> entidad existente (PUT). @MappingTarget modifica el producto que ya hay en vez de crear otro.
    // Con IGNORE, un campo que llegue a null (p. ej. descripcion) no borra el valor que ya tenía.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "disponible", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "alergenos", ignore = true)
    @Mapping(target = "lineas", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void actualizarEntidad(ProductoRequest request, @MappingTarget Producto producto);

    // MapStruct lo usa para convertir cada Alergeno de la colección en su nombre
    default String alergenoANombre(Alergeno alergeno) {
        return alergeno.getNombre();
    }
}
