package com.ProyectoFinal.CalculadoraHuellaCarbono.Dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Producto;

@Repository
@Transactional
public class ProductoDaoImp implements ProductoDao {

    @PersistenceContext
    EntityManager entityManager;

    // Método para obtener la lista de productos desde la base de datos
    
    @Override
    public List<Producto> getProductos() {
        String query = "FROM Producto";
        return entityManager.createQuery(query, Producto.class).getResultList();
    }

}