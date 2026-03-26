package com.appasistencia.exceptions;

// Excepcion: operacion invalida o regla de negocio violada (400)
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
