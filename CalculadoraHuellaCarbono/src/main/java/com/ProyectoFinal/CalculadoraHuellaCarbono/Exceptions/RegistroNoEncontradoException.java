package com.ProyectoFinal.CalculadoraHuellaCarbono.Exceptions;

/**
 * Excepción lanzada cuando se intenta operar sobre un registro
 * que no existe en la base de datos o no pertenece al usuario autenticado.
 */
public class RegistroNoEncontradoException extends RuntimeException {

    public RegistroNoEncontradoException(int id) {
        super("El registro con ID " + id + " no fue encontrado o no te pertenece.");
    }

    public RegistroNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
