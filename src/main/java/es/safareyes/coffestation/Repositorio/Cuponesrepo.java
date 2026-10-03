package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Cupones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface Cuponesrepo extends JpaRepository<Cupones, Long> {

}
