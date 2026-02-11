package com.appasistencia.dtos.response;

import com.appasistencia.models.DiaSemana;
import com.appasistencia.models.Horario;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class HorarioResponseDTO {

    private Long idHorario;
    private Long idAsignacion;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public HorarioResponseDTO() {}

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
