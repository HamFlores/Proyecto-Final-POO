package com.ProyectoFinal.CalculadoraHuellaCarbono.Dao;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Usuario;

public interface UsuarioDao {
    
    public void registrarUsuario(Usuario usuario);

    public boolean verificarCredenciales(Usuario usuario);
}
