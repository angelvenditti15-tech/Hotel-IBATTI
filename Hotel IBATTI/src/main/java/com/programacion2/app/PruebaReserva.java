package com.programacion2.app;

import com.programacion2.config.JdoConfig;
import com.programacion2.repositorio.ReservaRepositorio;
import com.programacion2.repositorio.ReservaRepositorio.FilaReserva;
import com.programacion2.repositorio.ReservaRepositorio.ReferenciaReserva;
import com.programacion2.servicio.ReservaService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.jdo.PersistenceManagerFactory;

public class PruebaReserva {

    private static final int CANTIDAD_PERSONAS = 2;

    public static void main(String[] args) {
        PersistenceManagerFactory fabrica = new JdoConfig().persistenceManagerFactory();

        try {
            ReservaRepositorio repositorio = new ReservaRepositorio(fabrica);
            ReservaService servicio = new ReservaService(repositorio);
            System.out.println(ejecutarComando(repositorio, servicio, args));
        } finally {
            fabrica.close();
        }
    }

    private static String ejecutarComando(ReservaRepositorio repositorio,
                                          ReservaService servicio, String[] args) {
        if (args.length == 5 && "crear".equals(args[0])) {
            int id = servicio.crearReserva(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),
                LocalDate.parse(args[3]),
                LocalDate.parse(args[4]),
                CANTIDAD_PERSONAS
            );

            return "Reserva guardada. ID: " + id;
        }

        if (args.length == 4 && "guardar".equals(args[0])) {
            ReferenciaReserva referencia = repositorio.buscarReferencia(Integer.parseInt(args[1]));
            int id = servicio.crearReserva(
                referencia.huespedId(),
                referencia.habitacionId(),
                LocalDate.parse(args[2]),
                LocalDate.parse(args[3]),
                CANTIDAD_PERSONAS
            );

            return "Reserva guardada. ID: " + id;
        }

        if (args.length == 0) {
            return consultarReserva(repositorio, 1);
        }

        if (args.length == 1 && !"crear".equals(args[0]) && !"guardar".equals(args[0])) {
            return consultarReserva(repositorio, Integer.parseInt(args[0]));
        }

        throw new IllegalArgumentException(
            "Usá ID_RESERVA para consultar, guardar ID_RESERVA INGRESO SALIDA, "
                + "o crear ID_HUESPED ID_HABITACION INGRESO SALIDA. "
                + "Formato de fechas: AAAA-MM-DD."
        );
    }

    private static String consultarReserva(ReservaRepositorio repositorio, int id) {
        FilaReserva reserva = repositorio.buscar(id);

        return "Reserva " + reserva.id()
            + " | Huésped: " + reserva.huesped()
            + " | Habitación: " + reserva.habitacion()
            + " | Hotel: " + reserva.hotel()
            + " | Ingreso: " + reserva.ingreso()
            + " | Salida: " + reserva.salida()
            + " | Noches: " + ChronoUnit.DAYS.between(reserva.ingreso(), reserva.salida())
            + " | Total: " + reserva.total();
    }
}