package gimnasio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseUpdateDTO {

    @NotBlank(message = "El tipo de clase es obligatorio")
    private String tipoClase;

    @NotEmpty(message = "Los días de la semana son obligatorios")
    private List<String> diasSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
    private Integer cupoMaximo;

    private String descripcion;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "ACTIVA|CERRADA", message = "El estado debe ser ACTIVA o CERRADA")
    private String estado;
}
