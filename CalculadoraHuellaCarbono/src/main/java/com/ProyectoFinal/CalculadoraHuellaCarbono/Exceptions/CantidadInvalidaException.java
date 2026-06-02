package com.ProyectoFinal.CalculadoraHuellaCarbono.Exceptions;


//Excepcion lanzada cuando el usuario envia una cantidad invalida

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
