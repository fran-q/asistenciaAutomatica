package com.appasistencia.dtos;

import com.appasistencia.models.UsuarioPlantillaBiometrica;

/**
 * DTO para exponer los datos de la relación usuario-plantilla biométrica. Evita
 * cualquier referencia a otras entidades salvo los IDs para prevenir ciclos.
 */
public class UsuarioPlantillaBiometricaDTO {
    private int id;
    private int usuarioId;
    private int plantillaBiometricaId;
    private boolean esActivo;

    public UsuarioPlantillaBiometricaDTO() {}

    public UsuarioPlantillaBiometricaDTO(UsuarioPlantillaBiometrica upb) {
        this.id = upb.getId();
        this.usuarioId = upb.getUsuario() != null ? upb.getUsuario().getId() : 0;
        this.plantillaBiometricaId = upb.getPlantillaBiometrica() != null ? upb.getPlantillaBiometrica().getId() : 0;
        this.esActivo = upb.isEsActivo();
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getPlantillaBiometricaId() { return plantillaBiometricaId; }
    public boolean isEsActivo() { return esActivo; }
}