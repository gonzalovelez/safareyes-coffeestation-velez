package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column (name = "nombre")
    private String Nombre;

    @Column (name = "email")
    private String Email;

    @Column (name = "dni")
    private String Dni;


}
