package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface Clienterepo extends JpaRepository<Cliente, Long> {

    //USANDO JPA INTERFACE (Q1)

    // Cliente por email, para comprobar cupones personales (RN-06) (findTop + IgnoreCase)
    Optional<Cliente> findTopByEmailIgnoreCase(String email);

    // ¿Hay ya un cliente con ese DNI? Para no registrar duplicados (existsBy)
    boolean existsByDni(String dni);

}
