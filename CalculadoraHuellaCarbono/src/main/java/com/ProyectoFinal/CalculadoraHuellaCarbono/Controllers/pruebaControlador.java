package com.ProyectoFinal.CalculadoraHuellaCarbono.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class pruebaControlador {
    @RequestMapping("/prueba")
    public String prueba(){
        return "Hola mundo";
    }
    
}
