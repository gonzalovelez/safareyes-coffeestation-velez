package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "nombre")
    private String nombre;

    @Column (name = "contraseña")
    private String contraseña;

    @Column (name = "rol")
    private String rol;

    // Lado inverso (Usuario → Cliente)
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Cliente cliente;


}
