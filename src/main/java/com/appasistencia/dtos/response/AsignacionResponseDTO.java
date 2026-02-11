package com.appasistencia.dtos.response;

import com.appasistencia.models.Asignacion;
import java.time.LocalDateTime;

public class AsignacionResponseDTO {

    private Long idAsignacion;
    private Long idProfesor;
    private Long idCursoMateria;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public AsignacionResponseDTO() {}

    public static AsignacionResponseDTO fromEntity(Asignacion asignacion) {
        AsignacionResponseDTO dto = new AsignacionResponseDTO();
        dto.idAsignacion = asignacion.getIdAsignacion();
        if (asignacion.getProfesor() != null) {
            dto.idProfesor = asignacion.getProfesor().getIdProfesor();
        }
        if (asignacion.getCursoMateria() != null) {
            dto.idCursoMateria = asignacion.getCursoMateria().getIdCursoMateria();
        }
        dto.fechaCreacion = asignacion.getFechaCreacion();
        dto.activo = asignacion.isActivo();
        return dto;
    }

    // Getters
    public Long getIdAsignacion() { return idAsignacion; }
    public Long getIdProfesor() { return idProfesor; }
    public Long getIdCursoMateria() { return idCursoMateria; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
