package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface Categoriarepo extends JpaRepository<Categoria, Long> {

}
