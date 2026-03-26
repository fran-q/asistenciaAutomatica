package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entidad: Inscripcion de un alumno a un curso
@Entity
@Table(name = "inscripcion", uniqueConstraints = @UniqueConstraint(columnNames = {"fk_id_alumno", "fk_id_curso"}))
public class Inscripcion {

    // Identificador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno_curso")
    private Long idAlumnoCurso;

    // Relaciones: alumno inscripto y curso destino
    @ManyToOne
    @JoinColumn(name = "fk_id_alumno")
    private UsuarioAlumno alumno;

    @ManyToOne
    @JoinColumn(name = "fk_id_curso")
    private Curso curso;

    // Auditoria
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
