package com.appasistencia.exceptions;

// Excepcion: recurso no encontrado (404)
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    // Genera mensaje automatico con nombre de entidad e ID
    public RecursoNoEncontradoException(String entidad, Long id) {
        super(entidad + " con ID " + id + " no fue encontrado/a");
    }
}
