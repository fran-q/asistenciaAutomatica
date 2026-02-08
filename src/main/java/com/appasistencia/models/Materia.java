package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "materia")
public class Materia {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Long idMateria;

    private String nombre;
    private String descripcion;

    @Column(name = "horas_semanales")
    private int horasSemanales;

    @ManyToOne
    @JoinColumn(name = "fk_id_carrera")
    private Carrera carrera;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL)
    private List<CursoMateria> cursoMaterias = new ArrayList<>();

    //Constructores
    public Materia() {}

    public Materia(String nombre, String descripcion, int horasSemanales, Carrera carrera) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horasSemanales = horasSemanales;
        this.carrera = carrera;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdMateria() {
        return idMateria;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getHorasSemanales() {
        return horasSemanales;
    }
    public void setHorasSemanales(int horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public Carrera getCarrera() {
        return carrera;
    }
    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
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

    public List<CursoMateria> getCursoMaterias() {
        return cursoMaterias;
    }
    public void setCursoMaterias(List<CursoMateria> cursoMaterias) {
        this.cursoMaterias = cursoMaterias;
    }
}
