package gimnasio.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MiembroResponseDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private String correo;
    private String telefono;
    private LocalDate fechaNacimiento;
    private LocalDate fechaRegistro;
    private String estado;



}
