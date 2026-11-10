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

// pasa de Producto (entidad) a DTO y al revés.
//
// aquí solo escribimos la interfaz. al compilar, MapStruct genera él solo la clase
// ProductoMapperImpl con todos los getX()/setX() copiando campo a campo
// (la puedes ver en target/generated-sources/annotations/.../mapper/ProductoMapperImpl.java).
// los campos que se llaman igual en los dos lados los copia sin decirle nada; con @Mapping
// le explicamos los que son distintos o los que tiene que saltarse (ignore = true).
// como en el pom le dijimos "componentModel=spring", esa clase es un bean y se inyecta en el servicio.
//
// la categoría y los alérgenos no se copian desde el request: ahí solo llegan ids
// (categoriaId, alergenosIds) y es el servicio el que busca las entidades en la bd.
@Mapper
public interface ProductoMapper {

    // entidad -> respuesta.
    // la categoría se queda solo con su nombre, y el set de alérgenos se convierte en una lista
    // de nombres (para cada alérgeno MapStruct llama a alergenoANombre, el método de abajo)
    @Mapping(target = "categoria", source = "categoria.nombre")
    @Mapping(target = "alergenos", source = "alergenos")
    ProductoResponse toResponse(Producto producto);

    // request -> producto nuevo (POST). lo que no viene en el request lo rellena el servicio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "disponible", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "alergenos", ignore = true)
    @Mapping(target = "lineas", ignore = true)
    Producto toEntity(ProductoRequest request);

    // request -> producto que ya existe (PUT).
    // @MappingTarget = no crees un producto nuevo, cambia los campos de este que te paso.
    // IGNORE = si un campo llega a null (p. ej. no mandan descripcion), no machaques el valor que ya tenía
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "disponible", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "alergenos", ignore = true)
    @Mapping(target = "lineas", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void actualizarEntidad(ProductoRequest request, @MappingTarget Producto producto);

    // MapStruct lo usa para pasar cada Alergeno a su nombre
    default String alergenoANombre(Alergeno alergeno) {
        return alergeno.getNombre();
    }
}
