package com.appasistencia.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de entrada: crear/editar materias de una carrera
public class MateriaDTO {

    // Datos de la materia
    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @Min(value = 1, message = "Las horas semanales mínimas son 1")
    @Max(value = 40, message = "Las horas semanales máximas son 40")
    private int horasSemanales;

    // Relacion (ID)
    @NotNull(message = "El ID de la carrera es obligatorio")
    private Long idCarrera;

    public MateriaDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getHorasSemanales() { return horasSemanales; }
    public void setHorasSemanales(int horasSemanales) { this.horasSemanales = horasSemanales; }

    public Long getIdCarrera() { return idCarrera; }
    public void setIdCarrera(Long idCarrera) { this.idCarrera = idCarrera; }
}
