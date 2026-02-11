package com.appasistencia.dtos;

import jakarta.validation.constraints.NotNull;

public class AsignacionDTO {

    @NotNull(message = "El ID del profesor es obligatorio")
    private Long idProfesor;

    @NotNull(message = "El ID de la relación curso-materia es obligatorio")
    private Long idCursoMateria;

    public AsignacionDTO() {}

    public Long getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Long idProfesor) { this.idProfesor = idProfesor; }

    public Long getIdCursoMateria() { return idCursoMateria; }
    public void setIdCursoMateria(Long idCursoMateria) { this.idCursoMateria = idCursoMateria; }
}
