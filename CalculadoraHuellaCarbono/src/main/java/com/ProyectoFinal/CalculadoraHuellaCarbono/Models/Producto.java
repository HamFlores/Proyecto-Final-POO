package com.ProyectoFinal.CalculadoraHuellaCarbono.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @Getter @Setter @Column(name = "id")
    private int id;

    @Getter @Setter @Column(name = "nombre")
    private String nombre;

    @Getter @Setter @Column(name = "empresa")
    private String empresa;

    @Getter @Setter @Column(name = "unidad")
    private String unidad;
    
    @Getter @Setter @Column(name = "huella_carbono")
    private float huellaCarbono;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    @Getter @Setter
    private Categoria categoria;

    public Producto() {
    }

    public Producto(int id, String nombre, String empresa, String unidad, float huellaCarbono, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.empresa = empresa;
        this.unidad = unidad;
        this.huellaCarbono = huellaCarbono;
        this.categoria = categoria;
    }

}
