package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alergeno", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Alergeno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "nombre")
    private String nombre;

    // Lado inverso (Alergeno → sus Productos)
    @ManyToMany(mappedBy = "alergenos", fetch = FetchType.LAZY)
    private Set<Producto> productos = new HashSet<>();

}
