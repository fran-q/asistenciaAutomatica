package com.appasistencia.dtos;

import com.appasistencia.models.PlantillaBiometrica;
import java.time.LocalDate;

public class PlantillaBiometricaDTO {
    //Atributos
    private int id;
    private String plantillaBiometrica;
    private LocalDate fechaCreacion;
    private boolean esActivo;

    //Constructores
    public PlantillaBiometricaDTO() {}

    public PlantillaBiometricaDTO(PlantillaBiometrica plantilla) {
        this.id = plantilla.getId();
        this.plantillaBiometrica = plantilla.getPlantillaBiometrica();
        this.fechaCreacion = plantilla.getFechaCreacion();
        this.esActivo = plantilla.isEsActivo();
    }

    //Getters
    public int getId() { return id; }
    public String getPlantillaBiometrica() { return plantillaBiometrica; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public boolean isEsActivo() { return esActivo; }
}