package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// línea de pedido: tabla intermedia entre pedido y producto.
// necesita entidad propia porque, además de las dos FK, guarda la cantidad.
@Entity
@Table(name = "producto_pedido", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class ProductoPedido {
    @EmbeddedId
    private ProductoPedidoId id = new ProductoPedidoId();

    // lado con la FK (ProductoPedido → Producto)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // lado con la FK (ProductoPedido → Pedido)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "cantidad")
    private Long cantidad;

}
