package com.appasistencia.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    private int status;
    private String mensaje;
    private LocalDateTime timestamp;
    private List<String> detalles;

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(int status, String mensaje, List<String> detalles) {
        this.status = status;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.detalles = detalles;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<String> detalles) {
        this.detalles = detalles;
    }
}
 