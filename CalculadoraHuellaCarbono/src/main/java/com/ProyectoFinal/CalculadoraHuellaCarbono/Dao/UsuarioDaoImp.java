package com.ProyectoFinal.CalculadoraHuellaCarbono.Dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Producto;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Usuario;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class UsuarioDaoImp implements UsuarioDao {

    @PersistenceContext
    EntityManager entityManager;
    
    @Override
    public void registrarUsuario(Usuario usuario) {
        // Registrar un nuevo usuario en la base de datos
        entityManager.merge(usuario);
    }

    @Override
    public Usuario obtenerUsuarioPorCredenciales(Usuario usuario) {
        // TODO Auto-generated method stub
        String query = "FROM Usuario WHERE correo_electronico = :email";
        List<Usuario> usuarios = entityManager.createQuery(query, Usuario.class)
                .setParameter("email", usuario.getCorreo_electronico())
                .getResultList();

        if (usuarios.isEmpty()) {
            return null; // No se encontró un usuario con ese correo electrónico
        }

        String hashedPassword = usuarios.get(0).getContraseña();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id); 

        if (argon2.verify(hashedPassword, usuario.getContraseña())) {
            return usuarios.get(0); // Devolver el usuario si las credenciales son válidas
        }

        return null; // Credenciales inválidas

    }
}
    