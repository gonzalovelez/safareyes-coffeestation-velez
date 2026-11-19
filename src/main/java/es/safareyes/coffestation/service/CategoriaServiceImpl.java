package es.safareyes.coffestation.service;

import es.safareyes.coffestation.Repositorio.Categoriarepo;
import es.safareyes.coffestation.dto.response.CategoriaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// lógica de las categorías
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final Categoriarepo categoriarepo;


    // endpoint 7 - GET /api/v1/categorias
    // url: http://localhost:8080/api/v1/categorias
    // aquí no hace falta mapper: la consulta ya devuelve el dto hecho (select new)
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        log.info("Listado de categorías");
        return categoriarepo.obtenerCategoriasConProductos();
    }
}
