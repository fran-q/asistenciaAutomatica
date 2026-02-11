package com.appasistencia.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PlantillaBiometricaDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @Min(value = 1, message = "La cantidad de muestras debe ser al menos 1")
    private int cantidadMuestras;

    public PlantillaBiometricaDTO() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public int getCantidadMuestras() { return cantidadMuestras; }
    public void setCantidadMuestras(int cantidadMuestras) { this.cantidadMuestras = cantidadMuestras; }
}
