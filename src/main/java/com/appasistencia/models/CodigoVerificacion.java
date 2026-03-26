package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entidad: codigo de verificacion por email para activar cuenta de admin
@Entity
@Table(name = "codigo_verificacion")
public class CodigoVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo")
    private Long idCodigo;

    // Codigo alfanumerico de 5 caracteres
    @Column(nullable = false, length = 5)
    private String codigo;

    // Fecha limite de validez (15 minutos desde creacion)
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    // Contador interno de intentos de verificacion (max 10)
    @Column(nullable = false)
    private int intentos = 0;

    // Usuario al que pertenece (uno a uno, unico)
    @OneToOne
    @JoinColumn(name = "fk_id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    public CodigoVerificacion() {}

    public CodigoVerificacion(String codigo, LocalDateTime fechaExpiracion, Usuario usuario) {
        this.codigo = codigo;
        this.fechaExpiracion = fechaExpiracion;
        this.usuario = usuario;
        this.intentos = 0;
    }

    public Long getIdCodigo() { return idCodigo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }
    public void incrementarIntentos() { this.intentos++; }
}
