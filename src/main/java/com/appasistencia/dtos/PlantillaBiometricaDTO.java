package com.appasistencia.dtos;

public class PlantillaBiometricaDTO {
    private Long idUsuario;
    private int cantidadMuestras;

    public PlantillaBiometricaDTO() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public int getCantidadMuestras() { return cantidadMuestras; }
    public void setCantidadMuestras(int cantidadMuestras) { this.cantidadMuestras = cantidadMuestras; }
}
