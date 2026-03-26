package com.appasistencia.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de entrada: crear/editar cursos (division concreta de una carrera en un anio lectivo)
public class CursoDTO {

    // Datos del curso
    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;

    @Min(value = 1, message = "El año de carrera mínimo es 1")
    @Max(value = 10, message = "El año de carrera máximo es 10")
    private int anioCarrera;

    @NotBlank(message = "La comisión es obligatoria")
    @Size(max = 50, message = "La comisión no puede superar los 50 caracteres")
    private String comision;

    @NotBlank(message = "El turno es obligatorio")
    private String turno;

    // Relacion (ID)
    @NotNull(message = "El ID de la carrera es obligatorio")
    private Long idCarrera;

    // Periodo lectivo
    @Min(value = 2000, message = "El año lectivo debe ser mayor o igual a 2000")
    @Max(value = 2100, message = "El año lectivo debe ser menor o igual a 2100")
    private int anioLectivo;

    public CursoDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getAnioCarrera() { return anioCarrera; }
    public void setAnioCarrera(int anioCarrera) { this.anioCarrera = anioCarrera; }

    public String getComision() { return comision; }
    public void setComision(String comision) { this.comision = comision; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Long getIdCarrera() { return idCarrera; }
    public void setIdCarrera(Long idCarrera) { this.idCarrera = idCarrera; }

    public int getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(int anioLectivo) { this.anioLectivo = anioLectivo; }
}
