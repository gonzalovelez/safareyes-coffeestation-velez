package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Cupones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface Cuponesrepo extends JpaRepository<Cupones, Long> {

    //USANDO JPA INTERFACE (Q1)

    // ¿Existe ya un cupón con ese código? Para no crear duplicados (existsBy + IgnoreCase)
    boolean existsByCodigoIgnoreCase(String codigo);

    // Busca el cupón por su código para validarlo (findFirst)
    Optional<Cupones> findFirstByCodigoIgnoreCase(String codigo);

    //EXTRA (JPQL): cupones de un cliente que están en vigor y aún tienen usos disponibles
    @Query("select c from Cupones c where c.cliente.id = :clienteId " +
            "and :ahora between c.fechaInicio and c.fechaFin " +
            "and size(c.usos) < c.maxUsos " +
            "order by c.fechaFin")
    List<Cupones> obtenerCuponesVigentesDeCliente(@Param("clienteId") Long clienteId,
                                                  @Param("ahora") LocalDateTime ahora);

}
