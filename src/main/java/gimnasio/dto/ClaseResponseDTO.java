package gimnasio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseResponseDTO {
    private Long id;
    private String tipoClase;
    private List<String> diasSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fechaInicio;
    private int cupoMaximo;
    private String descripcion;
    private String estado;

    // Relaciones (pueden ser null en clases creadas antes de agregar las relaciones)
    private Long categoriaId;
    private String categoriaNombre;
    private Long salaId;
    private String salaNombre;
    private Long entrenadorId;
    private String entrenadorNombre;
}
