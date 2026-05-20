package com.ProyectoFinal.CalculadoraHuellaCarbono.Dao;

import java.util.List;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.HistorialConsumoUsuarios;

public interface HistorialConsumoUsuariosDao {

    void registrarHistorialConsumoUsuarios(HistorialConsumoUsuarios historialConsumoUsuarios);

    List<HistorialConsumoUsuarios> getHistorialConsumoUsuarios(int usuarioId);

    boolean eliminarHistorialConsumoUsuarios(int historialId, int usuarioId);
    
}
