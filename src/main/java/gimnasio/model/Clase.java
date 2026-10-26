package gimnasio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los argumentos
@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoClase;

    @ElementCollection
    @CollectionTable(name = "clase_dias_semana", joinColumns = @JoinColumn(name = "clase_id"))
    @Column(name = "dia")
    private List<String> diasSemana;

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fechaInicio;
    private int cupoMaximo;
    private String descripcion;
    private String estado;
}
