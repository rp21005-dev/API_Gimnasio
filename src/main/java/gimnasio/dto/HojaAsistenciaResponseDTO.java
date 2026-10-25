package gimnasio.dto;

import java.time.LocalDate;
import java.util.List;

public class HojaAsistenciaResponseDTO {

    private Long claseId;
    private String tipoClase;
    private LocalDate fechaSesion;
    private int presentes;
    private int ausentes;
    private int sinRegistro;
    private List<AsistenciaResponseDTO> registros;

    public HojaAsistenciaResponseDTO() {
    }

    public Long getClaseId() {
        return claseId;
    }

    public void setClaseId(Long claseId) {
        this.claseId = claseId;
    }

    public String getTipoClase() {
        return tipoClase;
    }

    public void setTipoClase(String tipoClase) {
        this.tipoClase = tipoClase;
    }

    public LocalDate getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(LocalDate fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public int getPresentes() {
        return presentes;
    }

    public void setPresentes(int presentes) {
        this.presentes = presentes;
    }

    public int getAusentes() {
        return ausentes;
    }

    public void setAusentes(int ausentes) {
        this.ausentes = ausentes;
    }

    public int getSinRegistro() {
        return sinRegistro;
    }

    public void setSinRegistro(int sinRegistro) {
        this.sinRegistro = sinRegistro;
    }

    public List<AsistenciaResponseDTO> getRegistros() {
        return registros;
    }

    public void setRegistros(List<AsistenciaResponseDTO> registros) {
        this.registros = registros;
    }
}