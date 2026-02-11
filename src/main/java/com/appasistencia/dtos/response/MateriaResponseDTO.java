package com.appasistencia.dtos.response;

import com.appasistencia.models.Materia;
import java.time.LocalDateTime;

public class MateriaResponseDTO {

    private Long idMateria;
    private String nombre;
    private String descripcion;
    private int horasSemanales;
    private Long idCarrera;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public MateriaResponseDTO() {}

    public static MateriaResponseDTO fromEntity(Materia materia) {
        MateriaResponseDTO dto = new MateriaResponseDTO();
        dto.idMateria = materia.getIdMateria();
        dto.nombre = materia.getNombre();
        dto.descripcion = materia.getDescripcion();
        dto.horasSemanales = materia.getHorasSemanales();
        if (materia.getCarrera() != null) {
            dto.idCarrera = materia.getCarrera().getIdCarrera();
        }
        dto.fechaCreacion = materia.getFechaCreacion();
        dto.activo = materia.isActivo();
        return dto;
    }

    // Getters
    public Long getIdMateria() { return idMateria; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getHorasSemanales() { return horasSemanales; }
    public Long getIdCarrera() { return idCarrera; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
