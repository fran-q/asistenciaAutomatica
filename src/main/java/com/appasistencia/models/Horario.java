package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
public class Horario {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private DiaSemana diaSemana;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private boolean esActivo = true;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    //Constructores
    public Horario() {}

    public Horario(DiaSemana diaSemana, LocalTime horaEntrada, LocalTime horaSalida) {
        this.diaSemana = diaSemana;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.esActivo = true;
    }

    //Getters y Setters
    public int getId() {
        return id;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }
    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }
    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }
    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public boolean isEsActivo() {
        return esActivo;
    }
    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}