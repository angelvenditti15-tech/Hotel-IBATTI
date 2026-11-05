package com.programacion2.dominio;

import java.math.BigDecimal;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

@PersistenceCapable(detachable = "true")
@Unique(members = {"hotel", "numero"})
public class Habitacion {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private int id;

    @Persistent(defaultFetchGroup = "true")
    @Column(name = "HOTEL_ID", allowsNull = "false")
    private Hotel hotel;

    private int numero;

    @Column(jdbcType = "VARCHAR", length = 20, allowsNull = "false")
    private TipoHabitacion tipo;

    private int capacidad;

    @Column(jdbcType = "DECIMAL", length = 12, scale = 2, allowsNull = "false")
    private BigDecimal precioPorNoche;

    private boolean activa = true;

    public Habitacion() {
    }

    public Habitacion(Hotel hotel, int numero, TipoHabitacion tipo,
                      int capacidad, BigDecimal precioPorNoche) {
        this.hotel = hotel;
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precioPorNoche = precioPorNoche;
    }

    public int getId() {
        return id;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getPrecioPorNoche() {
        return precioPorNoche;
    }

    public void setPrecioPorNoche(BigDecimal precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}