package gimnasio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los argumentos
@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Uno de los tipos permitidos de la categoría
    @Column(nullable = false)
    private String tipoClase;

    @ElementCollection
    @CollectionTable(name = "clase_dias_semana", joinColumns = @JoinColumn(name = "clase_id"))
    @Column(name = "dia")
    private List<String> diasSemana = new ArrayList<>();

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fechaInicio;
    private int cupoMaximo;
    private String descripcion;

    // ACTIVA / CERRADA
    private String estado;

    // ---------- Relaciones ----------
    // Muchas clases pertenecen a una categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Muchas clases se imparten en una sala
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    private Sala sala;

    // Un entrenador imparte muchas clases
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;
}
