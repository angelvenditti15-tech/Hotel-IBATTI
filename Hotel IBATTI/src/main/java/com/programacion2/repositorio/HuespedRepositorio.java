package com.programacion2.repositorio;

import com.programacion2.dominio.Huesped;
import com.programacion2.dominio.Reserva;

import java.util.Comparator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.stereotype.Repository;

@Repository
public class HuespedRepositorio {

    private final PersistenceManagerFactory fabrica;

    public HuespedRepositorio(PersistenceManagerFactory fabrica) {
        this.fabrica = fabrica;
    }

    public List<FilaHuesped> listar() {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        try {
            Query<Huesped> consulta = gestor.newQuery(Huesped.class);
            try {
                return consulta.executeList().stream()
                    .map(huesped -> new FilaHuesped(
                        huesped.getId(), huesped.getNombre(),
                        huesped.getApellido(), huesped.getDni()
                    ))
                    .sorted(Comparator.comparingInt(FilaHuesped::id))
                    .toList();
            } finally {
                consulta.closeAll();
            }
        } finally {
            gestor.close();
        }
    }

    public DatosHuesped buscar(int id) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        try {
            Huesped huesped = gestor.getObjectById(Huesped.class, id);
            return new DatosHuesped(
                huesped.getNombre(), huesped.getApellido(), huesped.getDni(),
                huesped.getEmail(), huesped.getTelefono()
            );
        } finally {
            gestor.close();
        }
    }

    public void guardar(Huesped huesped) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();

        try {
            transaccion.begin();
            verificarDniDisponible(gestor, huesped.getDni(), null);
            gestor.makePersistent(huesped);
            transaccion.commit();
        } finally {
            cerrarEscritura(gestor, transaccion);
        }
    }

    public void actualizar(int id, Huesped datos) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();

        try {
            transaccion.begin();
            Huesped huesped = gestor.getObjectById(Huesped.class, id);
            verificarDniDisponible(gestor, datos.getDni(), id);

            huesped.setNombre(datos.getNombre());
            huesped.setApellido(datos.getApellido());
            huesped.setDni(datos.getDni());
            huesped.setEmail(datos.getEmail());
            huesped.setTelefono(datos.getTelefono());

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
            Huesped huesped = gestor.getObjectById(Huesped.class, id);

            Query<Reserva> consulta = gestor.newQuery(Reserva.class);
            try {
                boolean tieneReservas = consulta.executeList().stream()
                    .anyMatch(reserva -> reserva.getHuesped().getId() == id);

                if (tieneReservas) {
                    return false;
                }
            } finally {
                consulta.closeAll();
            }

            gestor.deletePersistent(huesped);
            transaccion.commit();
            return true;
        } finally {
            cerrarEscritura(gestor, transaccion);
        }
    }

    private void verificarDniDisponible(PersistenceManager gestor, String dni,
                                        Integer idActual) {
        Query<Huesped> consulta = gestor.newQuery(Huesped.class);
        try {
            consulta.setFilter("dni == dniBuscado");
            consulta.declareParameters("String dniBuscado");
            consulta.setParameters(dni);

            boolean ocupado = consulta.executeList().stream()
                .anyMatch(huesped -> idActual == null || huesped.getId() != idActual);

            if (ocupado) {
                throw new IllegalArgumentException("Ya existe un huésped con ese DNI.");
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

    public record FilaHuesped(int id, String nombre, String apellido, String dni) {}

    public record DatosHuesped(
        String nombre, String apellido, String dni, String email, String telefono
    ) {}
}