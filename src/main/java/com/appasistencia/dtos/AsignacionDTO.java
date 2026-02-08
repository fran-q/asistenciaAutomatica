package com.appasistencia.dtos;

public class AsignacionDTO {
    private Long idProfesor;
    private Long idCursoMateria;

    public AsignacionDTO() {}

    public Long getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Long idProfesor) { this.idProfesor = idProfesor; }

    public Long getIdCursoMateria() { return idCursoMateria; }
    public void setIdCursoMateria(Long idCursoMateria) { this.idCursoMateria = idCursoMateria; }
}
