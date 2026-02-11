package com.appasistencia.dtos;

import jakarta.validation.constraints.NotNull;

public class InscripcionDTO {

    @NotNull(message = "El ID del alumno es obligatorio")
    private Long idAlumno;

    @NotNull(message = "El ID del curso es obligatorio")
    private Long idCurso;

    public InscripcionDTO() {}

    public Long getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Long idAlumno) { this.idAlumno = idAlumno; }

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }
}
