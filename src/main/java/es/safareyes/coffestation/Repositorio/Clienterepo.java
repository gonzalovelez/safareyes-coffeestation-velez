package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface Clienterepo extends JpaRepository<Cliente, Long> {

}
