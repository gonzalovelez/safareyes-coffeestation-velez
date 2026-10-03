package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupones", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Cupones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column (name = "codigo")
    private String Codigo;

    @Column (name = "tipo")
    private String Tipo;

    @Column (name = "valor")
    private BigDecimal Valor;

    @Column(name = "fecha_inicio")
    private LocalDateTime Fecha_inicio;

    @Column (name = "fecha_fin")
    private LocalDateTime Fecha_fin;

    @Column (name = "max_usos")
    private Long Max_usos;

    @Column (name = "importe_minimo")
    private BigDecimal Importe_minimo;


}
