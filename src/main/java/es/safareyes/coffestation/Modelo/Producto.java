package es.safareyes.coffestation.Modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "producto", schema = "coffestation")
@Getter@Setter@NoArgsConstructor


public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "nombre")
    private String nombre;

    @Column (name = "descripcion")
    private String descripcion;

    @Column (name = "precio")
    private BigDecimal precio;

    @Column (name = "activo")
    private Boolean activo;

    @Column (name = "disponible")
    private Boolean disponible;

    // Lado con la FK (Producto → Categoria)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Lado propietario de la N:M (Producto → Alergenos, tabla alergeno_producto)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "alergeno_producto",
            schema = "coffestation",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "alergeno_id"))
    private Set<Alergeno> alergenos = new HashSet<>();

    // Lado inverso (Producto → sus líneas en ProductoPedido)
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private Set<ProductoPedido> lineas = new HashSet<>();


}
