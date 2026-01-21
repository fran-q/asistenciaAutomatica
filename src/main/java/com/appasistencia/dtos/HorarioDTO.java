package com.appasistencia.dtos;

import com.appasistencia.models.DiaSemana;
import com.appasistencia.models.Horario;

import java.io.DataInput;
import java.time.LocalTime;

public class HorarioDTO {
    //Atributos
    private int id;
    private DiaSemana diaSemana;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private boolean esActivo;

    //Construcotores
    public HorarioDTO() {}

    public HorarioDTO(Horario horario) {
        this.id = horario.getId();
        this.diaSemana = horario.getDiaSemana();
        this.horaEntrada = horario.getHoraEntrada();
        this.horaSalida = horario.getHoraSalida();
        this.esActivo = horario.isEsActivo();
    }

    //Getters
    public int getId() { return id; }
    public DiaSemana getDiaSemana() { return diaSemana; }
    public LocalTime getHoraEntrada() { return horaEntrada; }
    public LocalTime getHoraSalida() { return horaSalida; }
    public boolean isEsActivo() { return esActivo; }
}