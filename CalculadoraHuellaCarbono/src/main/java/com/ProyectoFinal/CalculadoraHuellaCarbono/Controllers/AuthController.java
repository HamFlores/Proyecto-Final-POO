package com.ProyectoFinal.CalculadoraHuellaCarbono.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Dao.UsuarioDao;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Usuario;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Utils.JWTUtil;

@RestController
public class AuthController {

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private JWTUtil jwtUtil;

    @RequestMapping(value = "/api/iniciarSesion", method = RequestMethod.POST)
    public String iniciarSesion(@RequestBody Usuario usuario) {
        // Implementation for user login
        Usuario usuarioLogueado = usuarioDao.obtenerUsuarioPorCredenciales(usuario);

        if (usuarioLogueado != null) {
            String tokenJWT = jwtUtil.create(String.valueOf(usuarioLogueado.getId()), usuarioLogueado.getCorreo_electronico());
            return tokenJWT;
        } else {
            return "FAIL";
        }
    }

}
