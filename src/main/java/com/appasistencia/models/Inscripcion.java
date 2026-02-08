package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno_curso")
    private Long idAlumnoCurso;

    @ManyToOne
    @JoinColumn(name = "fk_id_alumno")
    private UsuarioAlumno alumno;

    @ManyToOne
    @JoinColumn(name = "fk_id_curso")
    private Curso curso;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    //Constructores
    public Inscripcion() {}

    public Inscripcion(UsuarioAlumno alumno, Curso curso) {
        this.alumno = alumno;
        this.curso = curso;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdAlumnoCurso() {
        return idAlumnoCurso;
    }

    public UsuarioAlumno getAlumno() {
        return alumno;
    }
    public void setAlumno(UsuarioAlumno alumno) {
        this.alumno = alumno;
    }

    public Curso getCurso() {
        return curso;
    }
    public void setCurso(Curso curso) {
        this.curso = curso;
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
}
