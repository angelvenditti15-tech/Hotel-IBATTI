package com.programacion2.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Reserva {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private int id;

    @Persistent(defaultFetchGroup = "true")
    @Column(name = "HUESPED_ID", allowsNull = "false")
    private Huesped huesped;

    @Persistent(defaultFetchGroup = "true")
    @Column(name = "HABITACION_ID", allowsNull = "false")
    private Habitacion habitacion;

    @Column(allowsNull = "false")
    private LocalDate fechaIngreso;

    @Column(allowsNull = "false")
    private LocalDate fechaSalida;

    private int cantidadPersonas;

    @Column(jdbcType = "DECIMAL", length = 12, scale = 2, allowsNull = "false")
    private BigDecimal total;

    @Column(jdbcType = "VARCHAR", length = 20, allowsNull = "false")
    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(Huesped huesped, Habitacion habitacion, LocalDate fechaIngreso,
                   LocalDate fechaSalida, int cantidadPersonas, BigDecimal total) {
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.cantidadPersonas = cantidadPersonas;
        this.total = total;
        this.estado = EstadoReserva.CONFIRMADA;
    }

    public int getId() {
        return id;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public long calcularNoches() {
        return ChronoUnit.DAYS.between(fechaIngreso, fechaSalida);
    }
}