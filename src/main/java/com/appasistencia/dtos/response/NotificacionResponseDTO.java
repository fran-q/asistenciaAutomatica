package com.appasistencia.dtos.response;

import com.appasistencia.models.Notificacion;
import com.appasistencia.models.TipoNotificacion;
import java.time.LocalDateTime;

// DTO de respuesta: notificacion enviada a un alumno
public class NotificacionResponseDTO {

    private Long idNotificacion;
    // Relaciones (IDs planos) - asistencia y asignacion pueden ser null
    private Long idAlumno;
    private Long idAsistencia;
    private Long idAsignacion;
    // Contenido
    private TipoNotificacion tipo;
    private String titulo;
    private String mensaje;
    // Campos de auditoria
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public NotificacionResponseDTO() {}

    // Conversion desde entidad - extrae IDs planos de alumno, asistencia y asignacion
    public static NotificacionResponseDTO fromEntity(Notificacion notificacion) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.idNotificacion = notificacion.getIdNotificacion();
        if (notificacion.getAlumno() != null) {
            dto.idAlumno = notificacion.getAlumno().getIdAlumno();
        }
        if (notificacion.getAsistencia() != null) {
            dto.idAsistencia = notificacion.getAsistencia().getIdAsistencia();
        }
        if (notificacion.getAsignacion() != null) {
            dto.idAsignacion = notificacion.getAsignacion().getIdAsignacion();
        }
        dto.tipo = notificacion.getTipo();
        dto.titulo = notificacion.getTitulo();
        dto.mensaje = notificacion.getMensaje();
        dto.fechaCreacion = notificacion.getFechaCreacion();
        dto.activo = notificacion.isActivo();
        return dto;
    }

    // Getters
    public Long getIdNotificacion() { return idNotificacion; }
    public Long getIdAlumno() { return idAlumno; }
    public Long getIdAsistencia() { return idAsistencia; }
    public Long getIdAsignacion() { return idAsignacion; }
    public TipoNotificacion getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
