package com.appasistencia.exceptions;

// Excepcion: recurso duplicado (409 Conflict)
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
