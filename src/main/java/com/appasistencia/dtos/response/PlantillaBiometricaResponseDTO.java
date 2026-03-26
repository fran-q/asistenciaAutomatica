package com.appasistencia.dtos.response;

import com.appasistencia.models.PlantillaBiometrica;
import java.time.LocalDateTime;

// DTO de respuesta: plantilla biometrica facial de un usuario
public class PlantillaBiometricaResponseDTO {

    private Long idPlantillaBiometrica;
    // Relacion (ID plano)
    private Long idUsuario;
    private int cantidadMuestras;
    // Campos de auditoria
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public PlantillaBiometricaResponseDTO() {}

    // Conversion desde entidad - extrae idUsuario como ID plano
    public static PlantillaBiometricaResponseDTO fromEntity(PlantillaBiometrica plantilla) {
        PlantillaBiometricaResponseDTO dto = new PlantillaBiometricaResponseDTO();
        dto.idPlantillaBiometrica = plantilla.getIdPlantillaBiometrica();
        if (plantilla.getUsuario() != null) {
            dto.idUsuario = plantilla.getUsuario().getIdUsuario();
        }
        dto.cantidadMuestras = plantilla.getCantidadMuestras();
        dto.fechaCreacion = plantilla.getFechaCreacion();
        dto.activo = plantilla.isActivo();
        return dto;
    }

    // Getters
    public Long getIdPlantillaBiometrica() { return idPlantillaBiometrica; }
    public Long getIdUsuario() { return idUsuario; }
    public int getCantidadMuestras() { return cantidadMuestras; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
