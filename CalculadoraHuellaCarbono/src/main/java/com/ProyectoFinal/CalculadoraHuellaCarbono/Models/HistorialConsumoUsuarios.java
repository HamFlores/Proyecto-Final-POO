package com.ProyectoFinal.CalculadoraHuellaCarbono.Models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "historial_consumo_usuarios")
public class HistorialConsumoUsuarios {

    @Id
    @Getter @Setter @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @Getter @Setter
    private Usuario usuario;

    @ManyToMany
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    @Getter @Setter
    private Producto producto;

    @Getter @Setter @Column(name = "cantidad")
    private double cantidad;

    @Getter @Setter @Column(name = "huella_carbono_total")
    private double huellaCarbonoTotal;

    @Getter @Setter @Column(name = "fecha")
    private String fechaConsumo;

    
}
