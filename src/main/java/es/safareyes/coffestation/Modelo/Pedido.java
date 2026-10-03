package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column (name = "numero_turno")
    private Long Numero_turno;

    @Column (name = "fecha_hora")
    private LocalDateTime Fecha_hora;

    @Column (name = "base")
    private BigDecimal Base;

    @Column (name = "iva")
    private BigDecimal Iva;





}
