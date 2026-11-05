package com.programacion2.config;

import com.programacion2.dominio.Habitacion;
import com.programacion2.dominio.Hotel;
import com.programacion2.dominio.Huesped;
import com.programacion2.dominio.TipoHabitacion;
import java.math.BigDecimal;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final PersistenceManagerFactory fabrica;

    public DatosIniciales(PersistenceManagerFactory fabrica) {
        this.fabrica = fabrica;
    }

    @Override
    public void run(String... args) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();
        try {
            transaccion.begin();

            // 1. Verificar si ya existe el Hotel principal
            Query<Hotel> queryHotel = gestor.newQuery(Hotel.class);
            int totalHoteles;
            try {
                totalHoteles = queryHotel.executeList().size();
            } finally {
                queryHotel.closeAll();
            }

            if (totalHoteles == 0) {
                // Crear Hotel por defecto
                Hotel hotel = new Hotel("Hotel Ibatti", "Av. Argentina 123", "2994445555", "contacto@hotelibatti.com");
                gestor.makePersistent(hotel);

                // Crear Huéspedes de prueba
                Huesped h1 = new Huesped("Carlos", "Gómez", "35123456", "carlos@email.com", "2991112223");
                Huesped h2 = new Huesped("María", "Pérez", "38789456", "maria@email.com", "2993334445");
                gestor.makePersistent(h1);
                gestor.makePersistent(h2);

                // Crear Habitaciones asociadas al Hotel
                Habitacion hab1 = new Habitacion(hotel, 101, TipoHabitacion.INDIVIDUAL, 1, new BigDecimal("25000.00"));
                Habitacion hab2 = new Habitacion(hotel, 102, TipoHabitacion.DOBLE, 2, new BigDecimal("40000.00"));
                Habitacion hab3 = new Habitacion(hotel, 201, TipoHabitacion.SUITE, 3, new BigDecimal("75000.00"));
                
                gestor.makePersistent(hab1);
                gestor.makePersistent(hab2);
                gestor.makePersistent(hab3);

                System.out.println("[Hotel Ibatti] Datos iniciales cargados con éxito en la base de datos.");
            } else {
                System.out.println("[Hotel Ibatti] La base de datos ya contiene registros. Omitiendo carga inicial.");
            }

            transaccion.commit();
        } catch (Exception e) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            System.err.println("Error al cargar los datos iniciales: " + e.getMessage());
        } finally {
            gestor.close();
        }
    }
}