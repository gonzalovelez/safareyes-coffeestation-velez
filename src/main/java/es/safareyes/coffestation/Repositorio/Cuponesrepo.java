package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Cupones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface Cuponesrepo extends JpaRepository<Cupones, Long> {

    //USANDO JPA INTERFACE (Q1)

    // ¿Existe ya un cupón con ese código?
    boolean existsByCodigoIgnoreCase(String codigo);

    // Busca un cupón por su código
    Optional<Cupones> findFirstByCodigoIgnoreCase(String codigo);

}
