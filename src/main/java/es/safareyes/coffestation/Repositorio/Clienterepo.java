package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface Clienterepo extends JpaRepository<Cliente, Long> {

    // EXTRA: cliente por su email
    Optional<Cliente> findByEmail(String email);

}
