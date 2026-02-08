package com.appasistencia.dtos;

public class InscripcionDTO {
    private Long idAlumno;
    private Long idCurso;

    public InscripcionDTO() {}

    public Long getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Long idAlumno) { this.idAlumno = idAlumno; }

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }
}
