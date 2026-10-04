package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cliente", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "nombre")
    private String nombre;

    @Column (name = "email")
    private String email;

    @Column (name = "dni")
    private String dni;

    // Lado con la FK (Cliente → Usuario)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Lado inverso (Cliente → sus Pedidos)
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private Set<Pedido> pedidos = new HashSet<>();

    // Lado inverso (Cliente → sus Cupones)
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private Set<Cupones> cupones = new HashSet<>();


}
