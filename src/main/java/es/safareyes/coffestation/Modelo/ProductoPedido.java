package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Línea de pedido: tabla intermedia entre pedido y producto.
// Necesita entidad propia porque, además de las dos FK, guarda la cantidad.
@Entity
@Table(name = "producto_pedido", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class ProductoPedido {
    @EmbeddedId
    private ProductoPedidoId id = new ProductoPedidoId();

    // Lado con la FK (ProductoPedido → Producto)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Lado con la FK (ProductoPedido → Pedido)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "cantidad")
    private Long cantidad;

}
