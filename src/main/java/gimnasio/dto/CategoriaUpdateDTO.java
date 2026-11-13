package gimnasio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// PUT: se envía el recurso completo
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaUpdateDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El objetivo principal es obligatorio")
    private String objetivoPrincipal;

    @NotEmpty(message = "Debe indicar al menos un tipo de clase")
    private List<@NotBlank(message = "Los tipos de clase no pueden estar vacíos") String> tiposClase;
}
