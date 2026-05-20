package com.ProyectoFinal.CalculadoraHuellaCarbono.Models;

import lombok.Getter;
import lombok.Setter;

public class ConsumoRequest {

    @Getter @Setter
    private int productoId, categoriaId;
    @Getter @Setter
    private double cantidad;

}
