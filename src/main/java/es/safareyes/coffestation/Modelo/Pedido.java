package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedido", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "numero_turno")
    private Long numeroTurno;

    @Column (name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column (name = "base")
    private BigDecimal base;

    @Column (name = "iva")
    private BigDecimal iva;

    // Lado con la FK (Pedido → Cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Lado inverso (Pedido → sus líneas en ProductoPedido)
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private Set<ProductoPedido> lineas = new HashSet<>();

    // Lado inverso (Pedido → sus cupones en PedidoCupones)
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private Set<PedidoCupones> cupones = new HashSet<>();

    // Lado inverso (Pedido → sus Descuentos)
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private Set<Descuento> descuentos = new HashSet<>();





}
