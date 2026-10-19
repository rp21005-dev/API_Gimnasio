package gimnasio.dto;

import jakarta.validation.constraints.*;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
