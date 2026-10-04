package es.safareyes.coffestation.Repositorio;

import es.safareyes.coffestation.Modelo.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Coffestationconsultas {

    @Autowired
    private Productorepo productorepo;

    @Test
    void buscarProductosActivosPorCategoria() {

        List<Producto> productos =
                productorepo.findByCategoria_IdAndActivoTrue(1L);

        System.out.println("Productos encontrados: " + productos.size());

        for (Producto producto : productos) {
            System.out.println("ID: " + producto.getId());
            System.out.println("Nombre: " + producto.getNombre());
            System.out.println("Precio: " + producto.getPrecio());
        }
    }
}
