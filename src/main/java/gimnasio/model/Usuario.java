package gimnasio.model;
import  lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/// me genera los getters y setter de la clase
@Data
@AllArgsConstructor /// me genera el contructo con todos sus argumentos
@NoArgsConstructor /// Genera un contructor vacio en caso que lo ocupe mas adelante

public class Usuario {
    private Long id;
    private String correo;
    private String contrasenaHash;
    private String estado;
}

