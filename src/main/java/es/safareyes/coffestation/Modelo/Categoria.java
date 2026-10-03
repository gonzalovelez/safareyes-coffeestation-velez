package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column (name = "nombre")
    private String Nombre;

    @Column (name = "disponible")
    private Boolean Disponible;




}
