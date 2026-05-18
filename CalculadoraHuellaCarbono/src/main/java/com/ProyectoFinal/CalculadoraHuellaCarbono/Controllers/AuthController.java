package com.ProyectoFinal.CalculadoraHuellaCarbono.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Dao.UsuarioDao;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Usuario;

@RestController
public class AuthController {

    @Autowired
    private UsuarioDao usuarioDao;
    
    @RequestMapping(value = "/api/iniciarSesion", method = RequestMethod.POST)
    public String iniciarSesion(@RequestBody Usuario usuario) {
        // Implementation for user login
        if (usuarioDao.verificarCredenciales(usuario)) {
            return "Inicio de sesión exitoso";
        } else {
            return "Credenciales inválidas";
        }
    }

}
