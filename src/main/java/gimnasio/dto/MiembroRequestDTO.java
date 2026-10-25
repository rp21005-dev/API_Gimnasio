package gimnasio.dto;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MiembroRequestDTO {

    @NotBlank(message = "Los nombres son obligatorio")
    private String nombres;

    @NotBlank(message = "Los apellidos obligatorio")
    private String apellidos;

    @NotBlank(message = "El documento de identidad es obligatorio")
    @Size(max = 20)
    private String documentoIdentidad;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+\\- ]{8,20}$", message = "Formato de teléfono inválido")
    private String telefono;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;


    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "La contraseña debe tener mínimo 8 caracteres, al menos una letra y un número")
    private String contrasenaHash;


}
