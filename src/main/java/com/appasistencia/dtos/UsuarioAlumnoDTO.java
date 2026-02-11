package com.appasistencia.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioAlumnoDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @NotBlank(message = "El legajo es obligatorio")
    @Size(min = 2, max = 50, message = "El legajo debe tener entre 2 y 50 caracteres")
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
