package com.ProyectoFinal.CalculadoraHuellaCarbono.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Dao.UsuarioDao;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Usuario;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioDao usuarioDao;
    
    @RequestMapping(value = "/api/registroUsuario", method = RequestMethod.POST)
    public void registrarUsuario(@RequestBody Usuario usuario) {
        //metodo para cifrar la contraseña

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hashedPassword = argon2.hash(1, 1024, 1, usuario.getContraseña());
        usuario.setContraseña(hashedPassword);

        usuarioDao.registrarUsuario(usuario);
    }
}
