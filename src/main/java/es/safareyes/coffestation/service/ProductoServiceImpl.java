package es.safareyes.coffestation.service;

import es.safareyes.coffestation.Modelo.Alergeno;
import es.safareyes.coffestation.Modelo.Categoria;
import es.safareyes.coffestation.Modelo.Producto;
import es.safareyes.coffestation.Repositorio.Alergenorepo;
import es.safareyes.coffestation.Repositorio.Categoriarepo;
import es.safareyes.coffestation.Repositorio.ProductoSpecification;
import es.safareyes.coffestation.Repositorio.Productorepo;
import es.safareyes.coffestation.dto.request.DisponibilidadRequest;
import es.safareyes.coffestation.dto.request.ProductoRequest;
import es.safareyes.coffestation.dto.response.ProductoResponse;
import es.safareyes.coffestation.exception.RecursoNoEncontradoException;
import es.safareyes.coffestation.exception.ReglaNegocioException;
import es.safareyes.coffestation.mapper.ProductoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor // inyección por constructor de todos los atributos final
public class ProductoServiceImpl implements ProductoService {

    private final Productorepo productorepo;
    private final Categoriarepo categoriarepo;
    private final Alergenorepo alergenorepo;
    private final ProductoMapper productoMapper;

    // ---------------------------------------------------------------------------------------
    // Endpoint 1 - GET /api/v1/productos
    // readOnly: no hay escrituras, pero sí hace falta transacción para cargar los alérgenos (LAZY)
    // mientras se convierten a DTO (open-in-view está desactivado).
    // ---------------------------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarCarta(Long categoriaId, String texto, BigDecimal precioMax,
                                              Boolean disponible, Long sinAlergenoId, Pageable pageable) {
        log.info("Carta: categoria={}, texto={}, precioMax={}, disponible={}, sinAlergeno={}, pagina={}",
                categoriaId, texto, precioMax, disponible, sinAlergenoId, pageable);

        // RN-01: la especificación siempre incluye activo = true
        Specification<Producto> filtros =
                ProductoSpecification.carta(categoriaId, texto, precioMax, disponible, sinAlergenoId);

        return productorepo.findAll(filtros, pageable).map(productoMapper::toResponse);
    }

    // ---------------------------------------------------------------------------------------
    // Endpoint 2 - GET /api/v1/productos/{id}
    // ---------------------------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        log.info("Detalle del producto {}", id);

        // RN-01: un producto dado de baja ya no forma parte de la carta, así que se trata como inexistente
        Producto producto = productorepo.findWithAlergenosById(id)
                .filter(Producto::getActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));

        return productoMapper.toResponse(producto);
    }

    // ---------------------------------------------------------------------------------------
    // Endpoint 3 - POST /api/v1/productos
    // ---------------------------------------------------------------------------------------
    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        log.info("Alta de producto '{}'", request.nombre());

        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(buscarCategoria(request.categoriaId()));
        producto.setAlergenos(buscarAlergenos(request.alergenosIds()));
        // Un producto nuevo entra en la carta y con existencias
        producto.setActivo(true);
        producto.setDisponible(true);

        Producto guardado = productorepo.save(producto);
        log.info("Producto creado con id {}", guardado.getId());
        return productoMapper.toResponse(guardado);
    }

    // ---------------------------------------------------------------------------------------
    // Endpoint 4 - PUT /api/v1/productos/{id}
    // No hace falta llamar a save(): dentro de la transacción Hibernate detecta los cambios
    // en la entidad y lanza el UPDATE al terminar el método.
    // ---------------------------------------------------------------------------------------
    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        log.info("Modificación del producto {}", id);

        Producto producto = buscarProductoActivo(id);
        productoMapper.actualizarEntidad(request, producto);
        producto.setCategoria(buscarCategoria(request.categoriaId()));
        // Los alérgenos se sustituyen por los que llegan (si no llega ninguno, se quedan vacíos)
        producto.getAlergenos().clear();
        producto.getAlergenos().addAll(buscarAlergenos(request.alergenosIds()));

        // RN-03: cambiar el precio aquí no afecta a los pedidos antiguos, porque
        // cada línea de pedido guarda su propio precio unitario
        return productoMapper.toResponse(producto);
    }

    // ---------------------------------------------------------------------------------------
    // Endpoint 5 - PATCH /api/v1/productos/{id}/disponibilidad
    // ---------------------------------------------------------------------------------------
    @Override
    @Transactional
    public ProductoResponse cambiarDisponibilidad(Long id, DisponibilidadRequest request) {
        log.info("Producto {} -> disponible = {}", id, request.disponible());

        Producto producto = buscarProductoActivo(id);
        producto.setDisponible(request.disponible());
        return productoMapper.toResponse(producto);
    }

    // =======================================================================================
    // Métodos de apoyo
    // =======================================================================================

    // Busca un producto para modificarlo: 404 si no existe, 422 si está dado de baja (RN-01)
    private Producto buscarProductoActivo(Long id) {
        Producto producto = productorepo.findWithAlergenosById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
        if (!producto.getActivo()) {
            throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' (id " + id
                    + ") está dado de baja y no se puede modificar");
        }
        return producto;
    }

    // 404 si la categoría no existe
    private Categoria buscarCategoria(Long categoriaId) {
        return categoriarepo.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría con id " + categoriaId));
    }

    // Devuelve los alérgenos de la lista de ids; 404 indicando cuáles no existen
    private Set<Alergeno> buscarAlergenos(List<Long> alergenosIds) {
        if (alergenosIds == null || alergenosIds.isEmpty()) {
            return new HashSet<>();
        }
        // LinkedHashSet quita los ids repetidos y mantiene el orden
        Set<Long> idsPedidos = new LinkedHashSet<>(alergenosIds);
        List<Alergeno> encontrados = alergenorepo.findAllById(idsPedidos);

        if (encontrados.size() != idsPedidos.size()) {
            encontrados.forEach(alergeno -> idsPedidos.remove(alergeno.getId()));
            throw new RecursoNoEncontradoException("No existen los alérgenos con id " + idsPedidos);
        }
        return new HashSet<>(encontrados);
    }
}
