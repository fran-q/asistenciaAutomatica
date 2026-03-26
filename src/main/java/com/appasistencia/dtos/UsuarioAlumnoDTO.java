package com.appasistencia.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO de entrada: crear/editar perfil de alumno (vinculado a un Usuario existente)
public class UsuarioAlumnoDTO {

    // Relacion (ID) - usuario base al que se vincula el perfil
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    // Datos academicos del alumno
    @NotBlank(message = "El legajo es obligatorio")
    @Size(min = 4, max = 10, message = "El legajo debe tener entre 4 y 10 caracteres")
    @Pattern(regexp = "^[A-Za-z]?\\d{4,9}(/\\d{1,2})?$", message = "Formato de legajo invalido (ej: 12345, S12345, 59296/6)")
    private String legajo;

    @DecimalMin(value = "0.0", message = "El promedio no puede ser negativo")
    @DecimalMax(value = "10.0", message = "El promedio no puede ser mayor a 10")
    private Double promedio;

    public UsuarioAlumnoDTO() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public Double getPromedio() { return promedio; }
    public void setPromedio(Double promedio) { this.promedio = promedio; }
}
