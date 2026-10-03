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
    private Long Id;

    @Column (name = "nombre")
    private String Nombre;

    @Column (name = "contraseña")
    private String Contraseña;

    @Column (name = "rol")
    private String Rol;


}
