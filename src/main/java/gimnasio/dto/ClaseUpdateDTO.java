package gimnasio.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseUpdateDTO {
    private String tipoClase;
    private List<String> diasSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fechaInicio;
    private Integer cupoMaximo;
    private String descripcion;
    private String estado;
}