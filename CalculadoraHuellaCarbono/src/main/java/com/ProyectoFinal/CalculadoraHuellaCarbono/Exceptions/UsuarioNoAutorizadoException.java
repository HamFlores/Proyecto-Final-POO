package com.ProyectoFinal.CalculadoraHuellaCarbono.Exceptions;

//Excepción lanzada cuando un usuario intenta acceder a un recurso sin estar autenticado
public class UsuarioNoAutorizadoException extends RuntimeException {

    public UsuarioNoAutorizadoException() {
        super("Token inválido o sesión expirada. Inicia sesión nuevamente.");
    }

    public UsuarioNoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
