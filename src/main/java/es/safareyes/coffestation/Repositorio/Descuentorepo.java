package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface Descuentorepo extends JpaRepository<Descuento, Long> {

}
