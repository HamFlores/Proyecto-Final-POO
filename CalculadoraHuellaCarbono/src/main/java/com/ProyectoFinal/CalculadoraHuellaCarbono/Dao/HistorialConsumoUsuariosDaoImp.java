package com.ProyectoFinal.CalculadoraHuellaCarbono.Dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.HistorialConsumoUsuarios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class HistorialConsumoUsuariosDaoImp implements HistorialConsumoUsuariosDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registrarHistorialConsumoUsuarios(HistorialConsumoUsuarios historialConsumoUsuarios) {
        // TODO Auto-generated method stub
        entityManager.persist(historialConsumoUsuarios);
        
    }

    @Override
    public List<HistorialConsumoUsuarios> getHistorialConsumoUsuarios(int usuarioId) {
        //Query de Hibernate para obtener historial usasdo fecha mas reciente primero
        String query = "FROM HistorialConsumoUsuarios WHERE usuario.id = :usuarioId ORDER BY fecha DESC";
        return entityManager.createQuery(query, HistorialConsumoUsuarios.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();   
    }

    @Override
    public boolean eliminarHistorialConsumoUsuarios(int historialId, int usuarioId) {
        // Buscamos el registro para verificar que le pertenece al usuario
        HistorialConsumoUsuarios registro = entityManager.find(HistorialConsumoUsuarios.class, historialId);
        if (registro == null || registro.getUsuario().getId() != usuarioId) {
            return false; // No existe o no pertenece al usuario
        }
        entityManager.remove(registro);
        return true;
    }


}
