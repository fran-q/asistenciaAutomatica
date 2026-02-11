package com.appasistencia.dtos.response;

import com.appasistencia.models.Inscripcion;
import java.time.LocalDateTime;

public class InscripcionResponseDTO {

    private Long idAlumnoCurso;
    private Long idAlumno;
    private Long idCurso;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public InscripcionResponseDTO() {}

    public static InscripcionResponseDTO fromEntity(Inscripcion inscripcion) {
        InscripcionResponseDTO dto = new InscripcionResponseDTO();
        dto.idAlumnoCurso = inscripcion.getIdAlumnoCurso();
        if (inscripcion.getAlumno() != null) {
            dto.idAlumno = inscripcion.getAlumno().getIdAlumno();
        }
        if (inscripcion.getCurso() != null) {
            dto.idCurso = inscripcion.getCurso().getIdCurso();
        }
        dto.fechaCreacion = inscripcion.getFechaCreacion();
        dto.activo = inscripcion.isActivo();
        return dto;
    }

    // Getters
    public Long getIdAlumnoCurso() { return idAlumnoCurso; }
    public Long getIdAlumno() { return idAlumno; }
    public Long getIdCurso() { return idCurso; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
