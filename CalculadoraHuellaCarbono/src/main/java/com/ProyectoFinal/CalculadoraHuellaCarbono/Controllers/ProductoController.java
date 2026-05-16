package com.ProyectoFinal.CalculadoraHuellaCarbono.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Dao.ProductoDao;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Producto;

@RestController
public class ProductoController {

    @Autowired
    private ProductoDao productoDao;

    @RequestMapping(value = "/productos")
    public List<Producto> getProductos() {
        return productoDao.getProductos();
    }

}
