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
        // Utiliza el dao para verificar las credenciales del usuario
        Usuario usuarioLogueado = usuarioDao.obtenerUsuarioPorCredenciales(usuario);

        // Si el usuario es válido, genera un token JWT y lo devuelve
        //JWT es para saber que usuario esta logueado y para proteger las rutas del backend
        if (usuarioLogueado != null) {
            String tokenJWT = jwtUtil.create(String.valueOf(usuarioLogueado.getId()), usuarioLogueado.getCorreo_electronico());
            return tokenJWT;
        } else {
            return "FAIL";
        }
    }

}
