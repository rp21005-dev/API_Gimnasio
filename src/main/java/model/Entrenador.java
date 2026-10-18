package model;
import  lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getters/Setter
@NoArgsConstructor //Constructor vacio
@AllArgsConstructor // Contructo con los argumentos

public class Entrenador extends Usuario{
    private String nombres;
    private String apellidos;
    private String telefono;
    private String especialidad;

    /// Metodo para obtener nombre propio
    public String getNombreCompleto(){
        return  this.nombres + " " + this.apellidos;
    }
}


