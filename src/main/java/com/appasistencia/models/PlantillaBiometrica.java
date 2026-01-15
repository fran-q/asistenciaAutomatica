package com.appasistencia.models;

import jakarta.persistence.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PlantillaBiometrica {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String plantillaBiometrica;
    private LocalDate fechaCreacion;
    private boolean esActivo = true;
    @OneToMany(mappedBy = "plantillaBiometrica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioPlantillaBiometrica> usuarios = new ArrayList<>();

    //Constructores
    public PlantillaBiometrica() {}

    public PlantillaBiometrica(String plantillaBiometrica) {
        this.plantillaBiometrica = plantillaBiometrica;
        this.fechaCreacion = LocalDate.now();
        this.esActivo = true;
    }

    //Getters y Setters
    public int getId() {
        return id;
    }

    public String getPlantillaBiometrica() {
        return plantillaBiometrica;
    }
    public void setPlantillaBiometrica(String plantillaBiometrica) {
        this.plantillaBiometrica = plantillaBiometrica;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isEsActivo() {
        return esActivo;
    }
    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public void addUsuarioPlantillaBiometrica(UsuarioPlantillaBiometrica usuarioPlantilla) {
        usuarioPlantilla.setPlantillaBiometrica(this);
        this.usuarios.add(usuarioPlantilla);
    }
}