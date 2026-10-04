package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.PedidoCupones;
import es.safareyes.coffestation.Modelo.PedidoCuponesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface PedidoCuponesrepo extends JpaRepository<PedidoCupones, PedidoCuponesId> {

}
