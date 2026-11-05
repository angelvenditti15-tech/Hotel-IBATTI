package com.programacion2.repositorio;

import com.programacion2.dominio.Habitacion;
import com.programacion2.dominio.Hotel;
import com.programacion2.dominio.Reserva;
import com.programacion2.dominio.TipoHabitacion;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.stereotype.Repository;

@Repository
public class HabitacionRepositorio {

    private final PersistenceManagerFactory fabrica;

    public HabitacionRepositorio(PersistenceManagerFactory fabrica) {
        this.fabrica = fabrica;
    }

    public List<FilaHabitacion> listar() {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        try {
            Query<Habitacion> consulta = gestor.newQuery(Habitacion.class);
            try {
                return consulta.executeList().stream()
                    .map(habitacion -> new FilaHabitacion(
                        habitacion.getId(),
                        habitacion.getHotel().getNombre(),
                        habitacion.getNumero(),
                        habitacion.getTipo(),
                        habitacion.getCapacidad(),
                        habitacion.getPrecioPorNoche(),
                        habitacion.isActiva()
                    ))
                    .sorted(Comparator.comparing(FilaHabitacion::hotel)
                        .thenComparingInt(FilaHabitacion::numero))
                    .toList();
            } finally {
                consulta.closeAll();
            }
        } finally {
            gestor.close();
        }
    }

    public List<OpcionHotel> listarHoteles() {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        try {
            Query<Hotel> consulta = gestor.newQuery(Hotel.class);
            try {
                return consulta.executeList().stream()
                    .map(hotel -> new OpcionHotel(hotel.getId(), hotel.getNombre()))
                    .sorted(Comparator.comparing(OpcionHotel::nombre))
                    .toList();
            } finally {
                consulta.closeAll();
            }
        } finally {
            gestor.close();
        }
    }

    public DatosHabitacion buscar(int id) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        try {
            Habitacion habitacion = gestor.getObjectById(Habitacion.class, id);
            return new DatosHabitacion(
                habitacion.getHotel().getNombre(),
                habitacion.getNumero(),
                habitacion.getTipo(),
                habitacion.getCapacidad(),
                habitacion.getPrecioPorNoche()
            );
        } finally {
            gestor.close();
        }
    }

    public void guardar(int hotelId, int numero, TipoHabitacion tipo,
                        int capacidad, BigDecimal precioPorNoche) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();

        try {
            transaccion.begin();
            Hotel hotel;
            try {
                hotel = gestor.getObjectById(Hotel.class, hotelId);
            } catch (JDOObjectNotFoundException error) {
                throw new IllegalArgumentException("El hotel seleccionado ya no existe.", error);
            }

            verificarNumeroDisponible(gestor, hotelId, numero, null);
            gestor.makePersistent(new Habitacion(hotel, numero, tipo, capacidad, precioPorNoche));
            transaccion.commit();
        } finally {
            cerrarEscritura(gestor, transaccion);
        }
    }

    public void actualizar(int id, int numero, TipoHabitacion tipo,
                           int capacidad, BigDecimal precioPorNoche) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();

        try {
            transaccion.begin();
            Habitacion habitacion = gestor.getObjectById(Habitacion.class, id);
            verificarNumeroDisponible(gestor, habitacion.getHotel().getId(), numero, id);

            habitacion.setNumero(numero);
            habitacion.setTipo(tipo);
            habitacion.setCapacidad(capacidad);
            habitacion.setPrecioPorNoche(precioPorNoche);
            transaccion.commit();
        } finally {
            cerrarEscritura(gestor, transaccion);
        }
    }

    public boolean eliminarSiNoTieneReservas(int id) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();

        try {
            transaccion.begin();
            Habitacion habitacion = gestor.getObjectById(Habitacion.class, id);

            Query<Reserva> consulta = gestor.newQuery(Reserva.class);
            try {
                boolean tieneReservas = consulta.executeList().stream()
                    .anyMatch(reserva -> reserva.getHabitacion().getId() == id);

                if (tieneReservas) {
                    return false;
                }
            } finally {
                consulta.closeAll();
            }

            gestor.deletePersistent(habitacion);
            transaccion.commit();
            return true;
        } finally {
            cerrarEscritura(gestor, transaccion);
        }
    }

    private void verificarNumeroDisponible(PersistenceManager gestor, int hotelId,
                                           int numero, Integer idActual) {
        Query<Habitacion> consulta = gestor.newQuery(Habitacion.class);
        try {
            boolean ocupado = consulta.executeList().stream()
                .anyMatch(habitacion -> habitacion.getNumero() == numero
                    && habitacion.getHotel().getId() == hotelId
                    && (idActual == null || habitacion.getId() != idActual));

            if (ocupado) {
                throw new IllegalArgumentException(
                    "Ya existe una habitación con ese número en este hotel."
                );
            }
        } finally {
            consulta.closeAll();
        }
    }

    private void cerrarEscritura(PersistenceManager gestor, Transaction transaccion) {
        try {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
        } finally {
            gestor.close();
        }
    }

    public record FilaHabitacion(
        int id, String hotel, int numero, TipoHabitacion tipo,
        int capacidad, BigDecimal precioPorNoche, boolean activa
    ) {}

    public record OpcionHotel(int id, String nombre) {}

    public record DatosHabitacion(
        String hotel, int numero, TipoHabitacion tipo, int capacidad, BigDecimal precioPorNoche
    ) {}
}