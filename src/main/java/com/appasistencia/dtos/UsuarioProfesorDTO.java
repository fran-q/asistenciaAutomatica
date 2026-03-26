package com.appasistencia.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO de entrada: crear/editar perfil de profesor (vinculado a un Usuario existente)
public class UsuarioProfesorDTO {

    // Relacion (ID) - usuario base al que se vincula el perfil
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    // Datos academicos del profesor
    @NotBlank(message = "El legajo es obligatorio")
    @Size(min = 4, max = 10, message = "El legajo debe tener entre 4 y 10 caracteres")
    @Pattern(regexp = "^[A-Za-z]?\\d{4,9}(/\\d{1,2})?$", message = "Formato de legajo invalido (ej: 12345, S12345, 59296/6)")
    private String legajo;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres")
    private String titulo;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    public UsuarioProfesorDTO() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
