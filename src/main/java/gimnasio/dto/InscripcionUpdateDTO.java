package gimnasio.dto;

public class InscripcionUpdateDTO {

    private Long nuevaClaseId;

    public InscripcionUpdateDTO() {
    }

    public InscripcionUpdateDTO(Long nuevaClaseId) {
        this.nuevaClaseId = nuevaClaseId;
    }

    public Long getNuevaClaseId() {
        return nuevaClaseId;
    }

    public void setNuevaClaseId(Long nuevaClaseId) {
        this.nuevaClaseId = nuevaClaseId;
    }
}