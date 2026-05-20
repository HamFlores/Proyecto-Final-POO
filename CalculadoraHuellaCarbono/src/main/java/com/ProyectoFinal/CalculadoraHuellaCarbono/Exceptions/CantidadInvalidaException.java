package com.ProyectoFinal.CalculadoraHuellaCarbono.Exceptions;

/**
 * Excepción lanzada cuando el usuario envía una cantidad inválida
 * para registrar un consumo (por ejemplo, cero, negativa o nula).
 */
public class CantidadInvalidaException extends RuntimeException {

    public CantidadInvalidaException() {
        super("La cantidad debe ser un número mayor a cero.");
    }

    public CantidadInvalidaException(double cantidad) {
        super("La cantidad '" + cantidad + "' no es válida. Debe ser un número positivo mayor a cero.");
    }

    public CantidadInvalidaException(String mensaje) {
        super(mensaje);
    }
}
