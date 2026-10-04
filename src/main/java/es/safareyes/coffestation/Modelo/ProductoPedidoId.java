package es.safareyes.coffestation.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Clave primaria compuesta de producto_pedido: (producto_id, pedido_id)
@Embeddable
@Getter@Setter@NoArgsConstructor@AllArgsConstructor@EqualsAndHashCode


public class ProductoPedidoId implements Serializable {
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "pedido_id")
    private Long pedidoId;

}
