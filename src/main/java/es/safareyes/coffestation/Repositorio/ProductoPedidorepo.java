package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.ProductoPedido;
import es.safareyes.coffestation.Modelo.ProductoPedidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface ProductoPedidorepo extends JpaRepository<ProductoPedido, ProductoPedidoId> {

}
