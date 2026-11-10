package es.safareyes.coffestation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

// Formato JSON estable para las respuestas paginadas (Page<...>):
// { "content": [ ... ], "page": { "size", "number", "totalElements", "totalPages" } }
// Sin esto Spring serializa la clase interna PageImpl y avisa de que su formato puede cambiar.
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig {
}
