package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asignacion")
public class Asignacion {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Long idAsignacion;

    @ManyToOne
    @JoinColumn(name = "fk_id_profesor")
    private UsuarioProfesor profesor;

    @ManyToOne
    @JoinColumn(name = "fk_id_curso_materia")
    private CursoMateria cursoMateria;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "asignacion", cascade = CascadeType.ALL)
    private List<Horario> horarios = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "asignacion", cascade = CascadeType.ALL)
    private List<Asistencia> asistencias = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "asignacion", cascade = CascadeType.ALL)
    private List<Notificacion> notificaciones = new ArrayList<>();

    //Constructores
    public Asignacion() {}

    public Asignacion(UsuarioProfesor profesor, CursoMateria cursoMateria) {
        this.profesor = profesor;
        this.cursoMateria = cursoMateria;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdAsignacion() {
        return idAsignacion;
    }

    public UsuarioProfesor getProfesor() {
        return profesor;
    }
    public void setProfesor(UsuarioProfesor profesor) {
        this.profesor = profesor;
    }

    public CursoMateria getCursoMateria() {
        return cursoMateria;
    }
    public void setCursoMateria(CursoMateria cursoMateria) {
        this.cursoMateria = cursoMateria;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }
    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }

    public List<Asistencia> getAsistencias() {
        return asistencias;
    }
    public void setAsistencias(List<Asistencia> asistencias) {
        this.asistencias = asistencias;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }
    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }
}
