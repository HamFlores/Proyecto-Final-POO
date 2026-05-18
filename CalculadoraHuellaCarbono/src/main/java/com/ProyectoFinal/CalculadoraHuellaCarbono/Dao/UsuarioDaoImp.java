package com.ProyectoFinal.CalculadoraHuellaCarbono.Dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Producto;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Usuario;

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
        // Implementation for registering user
        entityManager.merge(usuario);
    }

    @Override
    public boolean verificarCredenciales(Usuario usuario) {
        // TODO Auto-generated method stub
        String query = "FROM Usuario WHERE correo_electronico = :email AND contraseña = :password";
        List<Usuario> usuarios = entityManager.createQuery(query, Usuario.class)
                .setParameter("email", usuario.getCorreo_electronico())
                .setParameter("password", usuario.getContraseña())
                .getResultList();

        return !usuarios.isEmpty();
    }
}
    