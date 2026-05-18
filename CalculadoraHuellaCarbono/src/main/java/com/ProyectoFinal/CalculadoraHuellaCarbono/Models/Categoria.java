package com.ProyectoFinal.CalculadoraHuellaCarbono.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "categorias")
@NoArgsConstructor
public class Categoria {

    @Id
    @Getter @Setter @Column(name = "id")
    private int id;

    @Getter @Setter @Column(name = "nombre")
    private String nombre;

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
}
