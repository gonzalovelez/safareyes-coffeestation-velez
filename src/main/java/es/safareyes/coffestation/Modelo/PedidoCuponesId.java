package es.safareyes.coffestation.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Clave primaria compuesta de pedido_cupones: (pedido_id, cupon_id)
@Embeddable
@Getter@Setter@NoArgsConstructor@AllArgsConstructor@EqualsAndHashCode


public class PedidoCuponesId implements Serializable {
    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "cupon_id")
    private Long cuponId;

}
