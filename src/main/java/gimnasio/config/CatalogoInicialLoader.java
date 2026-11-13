package gimnasio.config;

import gimnasio.model.Categoria;
import gimnasio.model.Sala;
import gimnasio.repository.CategoriaRepository;
import gimnasio.repository.SalaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

// Carga el catálogo inicial de categorías y algunas salas, solo si las tablas están vacías
@Configuration
public class CatalogoInicialLoader {

    @Bean
    CommandLineRunner cargarCatalogo(CategoriaRepository categoriaRepository, SalaRepository salaRepository) {
        return args -> {

            if (categoriaRepository.count() == 0) {
                categoriaRepository.saveAll(List.of(
                        categoria("Cardiovasculares (Cardio)",
                                "Resistencia aeróbica, quema calórica y salud cardiovascular.",
                                "Spinning / Indoor cycling", "Zumba", "Step", "Aeróbicos"),
                        categoria("Fuerza y Resistencia Muscular",
                                "Hipertrofia ligera, tono muscular y resistencia a la fatiga con cargas.",
                                "BodyPump", "GAP (Glúteos, Abdomen, Piernas)", "Power Bar", "Circuitos de pesas"),
                        categoria("Alta Intensidad (HIIT / Funcional)",
                                "Potencia, aceleración metabólica y capacidad anaeróbica en intervalos cortos.",
                                "Tabata", "CrossFit / Cross Training", "Boot Camp", "HIIT en cinta o funcional"),
                        categoria("Mente y Cuerpo (Bajo Impacto)",
                                "Flexibilidad, alineación postural, respiración y fuerza isométrica del core.",
                                "Pilates (Mat o Reformer)", "Yoga (Hatha, Vinyasa)", "BodyBalance", "Estiramiento / Movilidad"),
                        categoria("Combate y Artes Marciales",
                                "Coordinación, agilidad, reflejos y cardio de media-alta intensidad.",
                                "BodyCombat", "Kickboxing fitness", "Boxeo recreativo", "FitBoxing"),
                        categoria("Acuáticas (Aquafitness)",
                                "Resistencia muscular y cardiovascular sin impacto articular.",
                                "Aquagym", "AquaZumba", "AquaSpinning")
                ));
            }

            if (salaRepository.count() == 0) {
                salaRepository.saveAll(List.of(
                        sala("S-01", "Sala de Ciclismo", "Primer piso", 25, "Bicicletas fijas para spinning."),
                        sala("S-02", "Sala Multiuso", "Primer piso", 30, "Zumba, step, aeróbicos y funcional."),
                        sala("S-03", "Sala Mente y Cuerpo", "Segundo piso", 20, "Yoga, pilates y estiramiento.")
                ));
            }
        };
    }

    private Categoria categoria(String nombre, String objetivo, String... tipos) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setObjetivoPrincipal(objetivo);
        c.setTiposClase(new ArrayList<>(List.of(tipos)));
        return c;
    }

    private Sala sala(String codigo, String nombre, String ubicacion, int capacidad, String descripcion) {
        Sala s = new Sala();
        s.setCodigo(codigo);
        s.setNombre(nombre);
        s.setUbicacion(ubicacion);
        s.setCapacidadMaxima(capacidad);
        s.setDescripcion(descripcion);
        return s;
    }
}
