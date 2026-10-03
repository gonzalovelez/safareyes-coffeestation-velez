package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "producto", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column (name = "nombre")
    private String Nombre;

    @Column (name = "descripcion")
    private String Descripcion;

    @Column (name = "precio")
    private BigDecimal Precio;

    @Column (name = "activo")
    private Boolean Activo;

    @Column (name = "disponible")
    private Boolean Disponible;


}
