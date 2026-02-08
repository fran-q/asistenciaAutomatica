package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curso_materia")
public class CursoMateria {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso_materia")
    private Long idCursoMateria;

    @ManyToOne
    @JoinColumn(name = "fk_id_curso")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "fk_id_materia")
    private Materia materia;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "cursoMateria", cascade = CascadeType.ALL)
    private List<Asignacion> asignaciones = new ArrayList<>();

    //Constructores
    public CursoMateria() {}

    public CursoMateria(Curso curso, Materia materia) {
        this.curso = curso;
        this.materia = materia;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdCursoMateria() {
        return idCursoMateria;
    }

    public Curso getCurso() {
        return curso;
    }
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Materia getMateria() {
        return materia;
    }
    public void setMateria(Materia materia) {
        this.materia = materia;
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

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }
    public void setAsignaciones(List<Asignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }
}
