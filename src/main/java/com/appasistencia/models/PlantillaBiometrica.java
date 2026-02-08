package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plantilla_biometrica")
public class PlantillaBiometrica {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla_biometrica")
    private Long idPlantillaBiometrica;

    @ManyToOne
    @JoinColumn(name = "fk_id_usuario")
    private Usuario usuario;

    @Lob
    @Column(name = "modelo_facial", columnDefinition = "LONGBLOB")
    private byte[] modeloFacial;

    @Column(name = "cantidad_muestras")
    private int cantidadMuestras;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    //Constructores
    public PlantillaBiometrica() {}

    public PlantillaBiometrica(Usuario usuario, byte[] modeloFacial, int cantidadMuestras) {
        this.usuario = usuario;
        this.modeloFacial = modeloFacial;
        this.cantidadMuestras = cantidadMuestras;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdPlantillaBiometrica() {
        return idPlantillaBiometrica;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public byte[] getModeloFacial() {
        return modeloFacial;
    }
    public void setModeloFacial(byte[] modeloFacial) {
        this.modeloFacial = modeloFacial;
    }

    public int getCantidadMuestras() {
        return cantidadMuestras;
    }
    public void setCantidadMuestras(int cantidadMuestras) {
        this.cantidadMuestras = cantidadMuestras;
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
}
