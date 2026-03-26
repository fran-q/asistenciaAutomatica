package com.appasistencia.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de entrada: crear notificaciones para alumnos (inasistencias, alertas, etc.)
public class NotificacionDTO {

    // Relaciones (IDs) - asistencia y asignacion son opcionales segun el tipo
    @NotNull(message = "El ID del alumno es obligatorio")
    private Long idAlumno;

    private Long idAsistencia;

    private Long idAsignacion;

    // Contenido de la notificacion
    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String tipo;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 2, max = 200, message = "El título debe tener entre 2 y 200 caracteres")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 2, max = 1000, message = "El mensaje debe tener entre 2 y 1000 caracteres")
    private String mensaje;

    public NotificacionDTO() {}

    public Long getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Long idAlumno) { this.idAlumno = idAlumno; }

    public Long getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Long idAsistencia) { this.idAsistencia = idAsistencia; }

    public Long getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(Long idAsignacion) { this.idAsignacion = idAsignacion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
