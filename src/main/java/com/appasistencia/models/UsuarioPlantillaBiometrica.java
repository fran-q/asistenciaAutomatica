package com.appasistencia.models;

import jakarta.persistence.*;

@Entity
public class UsuarioPlantillaBiometrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "plantilla_biometrica_id")
    private PlantillaBiometrica plantillaBiometrica;

    private boolean esActivo = true;

    public UsuarioPlantillaBiometrica() {}

    public boolean isEsActivo() {
        return esActivo;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public int getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public PlantillaBiometrica getPlantillaBiometrica() {
        return plantillaBiometrica;
    }

    public void setPlantillaBiometrica(PlantillaBiometrica plantillaBiometrica) {
        this.plantillaBiometrica = plantillaBiometrica;
    }
}