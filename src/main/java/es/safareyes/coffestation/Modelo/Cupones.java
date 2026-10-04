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
@Table(name = "cupones", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Cupones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "codigo")
    private String codigo;

    @Column (name = "tipo")
    private String tipo;

    @Column (name = "valor")
    private BigDecimal valor;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column (name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column (name = "max_usos")
    private Long maxUsos;

    @Column (name = "importe_minimo")
    private BigDecimal importeMinimo;

    // Lado con la FK (Cupones → Cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Lado inverso (Cupones → sus usos en PedidoCupones)
    @OneToMany(mappedBy = "cupon", fetch = FetchType.LAZY)
    private Set<PedidoCupones> usos = new HashSet<>();


}
