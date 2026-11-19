package es.safareyes.coffestation.service;

import es.safareyes.coffestation.dto.response.CategoriaResponse;

import java.util.List;

// lo que se puede hacer con las categorías (de momento solo el endpoint 7)
public interface CategoriaService {

    // endpoint 7: todas las categorías con su nº de productos activos
    List<CategoriaResponse> listar();
}
