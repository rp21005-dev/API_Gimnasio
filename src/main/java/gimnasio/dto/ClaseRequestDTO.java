package gimnasio.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseRequestDTO {

    @NotBlank(message = "El tipo de clase es obligatorio")
    private String tipoClase;

    @NotEmpty(message = "Debe indicar al menos un día de la semana")
    private List<@NotBlank(message = "Los días no pueden estar vacíos") String> diasSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a hoy")
    private LocalDate fechaInicio;

    @Min(value = 1, message = "El cupo máximo debe ser mayor a cero")
    private int cupoMaximo;

    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La sala es obligatoria")
    private Long salaId;

    @NotNull(message = "El entrenador es obligatorio")
    private Long entrenadorId;
}
