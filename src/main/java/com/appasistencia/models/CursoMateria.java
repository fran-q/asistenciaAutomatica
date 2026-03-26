package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad: Vinculacion entre un curso y una materia
@Entity
@Table(name = "curso_materia", uniqueConstraints = @UniqueConstraint(columnNames = {"fk_id_curso", "fk_id_materia"}))
public class CursoMateria {

    // Identificador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso_materia")
    private Long idCursoMateria;

    // Relaciones: curso y materia que se vinculan
    @ManyToOne
    @JoinColumn(name = "fk_id_curso")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "fk_id_materia")
    private Materia materia;

    // Auditoria
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    // Asignaciones de profesores a esta curso-materia
    @JsonIgnore
    @OneToMany(mappedBy = "cursoMateria", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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
