package com.ProyectoFinal.CalculadoraHuellaCarbono.Models;

import jakarta.persistence.GeneratedValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @Getter @Setter
    @JsonIgnoreProperties({"historialConsumoUsuarios"})
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "id", nullable = true)
    @Getter @Setter
    @JsonIgnoreProperties({"historialConsumoUsuarios"})
    private Producto producto;

    @Getter @Setter @Column(name = "cantidad")
    private double cantidad;

    // Spring Boot's naming strategy convierte "huellaTotal" -> "huella_total" en SQL.
    // La columna "huella_total" ya existe en la BD (añadida por Hibernate con ddl-auto=update).
    @Getter @Setter @Column(name = "huella_total")
    private double huellaCarbonoTotal;

    // Guardamos como String "YYYY-MM-DD" para evitar problemas de parseo de fechas
    // con el driver JDBC de SQLite (que almacena LocalDate como epoch en milisegundos).
    @Getter @Setter @Column(name = "fecha")
    private String fecha;

}
