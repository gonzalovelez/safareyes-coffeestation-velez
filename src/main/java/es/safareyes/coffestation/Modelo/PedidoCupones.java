package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// cupón aplicado a un pedido: tabla intermedia entre pedido y cupones.
// necesita entidad propia porque, además de las dos FK, guarda el descuento aplicado.
@Entity
@Table(name = "pedido_cupones", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class PedidoCupones {
    @EmbeddedId
    private PedidoCuponesId id = new PedidoCuponesId();

    // lado con la FK (PedidoCupones → Pedido)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    // lado con la FK (PedidoCupones → Cupones)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cuponId")
    @JoinColumn(name = "cupon_id")
    private Cupones cupon;

    @Column(name = "descuento")
    private BigDecimal descuento;

}
