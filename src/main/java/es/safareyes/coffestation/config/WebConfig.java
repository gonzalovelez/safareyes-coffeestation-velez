package es.safareyes.coffestation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

// hace que las respuestas paginadas (Page<...>) salgan siempre con este json:
// { "content": [ ... ], "page": { "size", "number", "totalElements", "totalPages" } }
//
// si no ponemos esto, Spring convierte a json la clase PageImpl tal cual, con un montón de campos
// internos, y encima saca un aviso en consola diciendo que ese formato puede cambiar en otra versión.
// con VIA_DTO usa un formato fijo y más limpio.
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig {
}
