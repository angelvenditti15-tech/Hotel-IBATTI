package com.programacion2.servicio;

import com.programacion2.dominio.TipoHabitacion;
import com.programacion2.repositorio.HabitacionRepositorio;
import com.programacion2.repositorio.HabitacionRepositorio.DatosHabitacion;
import com.programacion2.repositorio.HabitacionRepositorio.FilaHabitacion;
import com.programacion2.repositorio.HabitacionRepositorio.OpcionHotel;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HabitacionService {

    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("9999999999.99");

    private final HabitacionRepositorio repositorio;

    public HabitacionService(HabitacionRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<FilaHabitacion> listar() {
        return repositorio.listar();
    }

    public List<OpcionHotel> listarHoteles() {
        return repositorio.listarHoteles();
    }

    public DatosHabitacion buscar(int id) {
        return repositorio.buscar(id);
    }

    public void registrar(int hotelId, int numero, TipoHabitacion tipo,
                          int capacidad, BigDecimal precioPorNoche) {
        if (hotelId < 1) {
            throw new IllegalArgumentException("Elegí un hotel.");
        }

        validarDatos(numero, tipo, capacidad, precioPorNoche);
        repositorio.guardar(hotelId, numero, tipo, capacidad, precioPorNoche.setScale(2));
    }

    public void actualizar(int id, int numero, TipoHabitacion tipo,
                           int capacidad, BigDecimal precioPorNoche) {
        validarDatos(numero, tipo, capacidad, precioPorNoche);
        repositorio.actualizar(id, numero, tipo, capacidad, precioPorNoche.setScale(2));
    }

    public void eliminar(int id) {
        if (!repositorio.eliminarSiNoTieneReservas(id)) {
            throw new IllegalStateException(
                "No se puede eliminar la habitación porque tiene reservas asociadas."
            );
        }
    }

    private void validarDatos(int numero, TipoHabitacion tipo,
                              int capacidad, BigDecimal precioPorNoche) {
        if (tipo == null) {
            throw new IllegalArgumentException("Elegí un tipo de habitación.");
        }

        if (numero < 1 || capacidad < 1) {
            throw new IllegalArgumentException(
                "El número y la capacidad deben ser mayores que cero."
            );
        }

        if (precioPorNoche == null || precioPorNoche.signum() <= 0
                || precioPorNoche.stripTrailingZeros().scale() > 2
                || precioPorNoche.compareTo(PRECIO_MAXIMO) > 0) {
            throw new IllegalArgumentException(
                "Ingresá un precio positivo de hasta 10 dígitos enteros y 2 decimales."
            );
        }
    }
}