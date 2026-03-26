package com.appasistencia.dtos.response;

import com.appasistencia.models.DiaSemana;
import com.appasistencia.models.Horario;
import java.time.LocalDateTime;
import java.time.LocalTime;

// DTO de respuesta: bloque horario de una asignacion
public class HorarioResponseDTO {

    private Long idHorario;
    // Relacion (ID plano)
    private Long idAsignacion;
    // Datos del horario
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    // Campos de auditoria
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public HorarioResponseDTO() {}

    // Conversion desde entidad - extrae idAsignacion como ID plano
    public static HorarioResponseDTO fromEntity(Horario horario) {
        HorarioResponseDTO dto = new HorarioResponseDTO();
        dto.idHorario = horario.getIdHorario();
        if (horario.getAsignacion() != null) {
            dto.idAsignacion = horario.getAsignacion().getIdAsignacion();
        }
        dto.diaSemana = horario.getDiaSemana();
        dto.horaInicio = horario.getHoraInicio();
        dto.horaFin = horario.getHoraFin();
        dto.fechaCreacion = horario.getFechaCreacion();
        dto.activo = horario.isActivo();
        return dto;
    }

    // Getters
    public Long getIdHorario() { return idHorario; }
    public Long getIdAsignacion() { return idAsignacion; }
    public DiaSemana getDiaSemana() { return diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
