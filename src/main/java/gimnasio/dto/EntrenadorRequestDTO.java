package gimnasio.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorRequestDTO {

    @NotBlank(message = "Los nombres son obligatorio")
    private String nombres;

    @NotBlank(message = "Los apellidos obligatorio")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+\\- ]{8,20}$", message = "Formato de teléfono inválido")
    private String telefono;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;


    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "La contraseña debe tener mínimo 8 caracteres, al menos una letra y un número")
    private String contrasenaHash;



}
