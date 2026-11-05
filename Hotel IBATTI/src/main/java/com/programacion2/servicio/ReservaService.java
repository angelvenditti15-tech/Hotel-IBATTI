package com.programacion2.servicio;

import com.programacion2.dominio.EstadoReserva;
import com.programacion2.dominio.Habitacion;
import com.programacion2.dominio.Reserva;
import com.programacion2.repositorio.ReservaRepositorio;
import com.programacion2.repositorio.ReservaRepositorio.FilaReserva;
import com.programacion2.repositorio.ReservaRepositorio.OpcionesReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;

import org.springframework.stereotype.Service;

@Service

public class ReservaService {

    private final ReservaRepositorio repositorio;

    public ReservaService(ReservaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<FilaReserva> listarPorHabitacion(String habitacion) {
        String numero = habitacion.strip();

        return repositorio.listar().stream()
            .filter(reserva -> numero.isEmpty() || reserva.habitacion().equalsIgnoreCase(numero))
            .sorted(Comparator.comparingInt(FilaReserva::id))
            .toList();
    }
public void eliminar(Long id) {
    repositorio.eliminar(id);
}
    public OpcionesReserva cargarOpciones() {
        return repositorio.cargarOpciones();
    }

    public int crearReserva(int huespedId, int habitacionId, LocalDate ingreso,
                            LocalDate salida, int personas) {
        if (personas < 1) {
            throw new IllegalArgumentException("Ingresá al menos una persona.");
        }

        try {
            return repositorio.guardar(huespedId, habitacionId, datos -> {
                Habitacion habitacion = datos.habitacion();

                if (!habitacion.isActiva()) {
                    throw new IllegalArgumentException(
                        "La habitación seleccionada está inactiva."
                    );
                }

                if (personas > habitacion.getCapacidad()) {
                    throw new IllegalArgumentException(
                        "La habitación " + habitacion.getNumero()
                            + " admite hasta " + habitacion.getCapacidad() + " personas."
                    );
                }

                validarDisponibilidad(habitacionId, ingreso, salida, datos.existentes());

                long noches = ChronoUnit.DAYS.between(ingreso, salida);
                BigDecimal total = habitacion.getPrecioPorNoche()
                    .multiply(BigDecimal.valueOf(noches));

                return new Reserva(datos.huesped(), habitacion, ingreso, salida, personas, total);
            });
        } catch (JDOObjectNotFoundException error) {
            throw new IllegalArgumentException(
                "El huésped o la habitación seleccionada ya no está disponible.", error
            );
        }
    }
public void actualizarReserva(int id, int huespedId, int habitacionId, LocalDate ingreso,
                                LocalDate salida, int personas) {
        if (personas < 1) {
            throw new IllegalArgumentException("Ingresá al menos una persona.");
        }

        try {
            // Buscamos la reserva existente en el repositorio o la actualizamos
            repositorio.guardarOActualizar(id, huespedId, habitacionId, datos -> {
                Habitacion habitacion = datos.habitacion();

                if (!habitacion.isActiva()) {
                    throw new IllegalArgumentException("La habitación seleccionada está inactiva.");
                }

                if (personas > habitacion.getCapacidad()) {
                    throw new IllegalArgumentException(
                        "La habitación " + habitacion.getNumero()
                            + " admite hasta " + habitacion.getCapacidad() + " personas."
                    );
                }

                validarDisponibilidad(habitacionId, ingreso, salida, datos.existentes());

                long noches = ChronoUnit.DAYS.between(ingreso, salida);
                BigDecimal total = habitacion.getPrecioPorNoche()
                    .multiply(BigDecimal.valueOf(noches));

                return new Reserva(datos.huesped(), habitacion, ingreso, salida, personas, total);
            });
        } catch (JDOObjectNotFoundException error) {
            throw new IllegalArgumentException(
                "El huésped o la habitación seleccionada ya no está disponible.", error
            );
        }
    }
    public void validarDisponibilidad(int habitacionId, LocalDate ingreso,
                                      LocalDate salida, Iterable<Reserva> reservasExistentes) {
        if (ingreso == null || salida == null) {
            throw new IllegalArgumentException("Ingresá las dos fechas.");
        }

        if (!salida.isAfter(ingreso)) {
            throw new IllegalArgumentException(
                "La fecha de salida tiene que ser posterior a la de ingreso."
            );
        }

        if (ingreso.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "La fecha de ingreso no puede estar en el pasado."
            );
        }

        for (Reserva existente : reservasExistentes) {
            boolean mismaHabitacion = existente.getHabitacion().getId() == habitacionId;
            boolean activa = existente.getEstado() == EstadoReserva.CONFIRMADA
                || existente.getEstado() == EstadoReserva.EN_CURSO;

            if (mismaHabitacion && activa
                    && ingreso.isBefore(existente.getFechaSalida())
                    && salida.isAfter(existente.getFechaIngreso())) {
                throw new IllegalStateException(
                    "La habitación ya tiene una reserva para esas fechas."
                    
                );
            }
        }

    }
    
}

