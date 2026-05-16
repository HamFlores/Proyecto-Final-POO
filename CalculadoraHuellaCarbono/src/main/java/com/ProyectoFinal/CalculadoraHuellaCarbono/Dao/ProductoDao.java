package com.ProyectoFinal.CalculadoraHuellaCarbono.Dao;

import java.util.List;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Producto;

public interface ProductoDao {
    
    List<Producto> getProductos();
}
