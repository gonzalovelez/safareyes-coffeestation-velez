package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface Usuariorepo extends JpaRepository<Usuario, Long> {

}
