package com.programacion2.app;

import com.programacion2.dominio.Hotel;
import java.util.Map;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

public class PruebaJdo {

    public static void main(String[] args) {
        String clave = System.getenv("HOTEL_DB_PASSWORD");

        if (clave == null || clave.isEmpty()) {
            throw new IllegalStateException("Falta configurar HOTEL_DB_PASSWORD.");
        }

        Map<String, String> conexion = Map.of(
            "javax.jdo.option.ConnectionDriverName", "com.mysql.cj.jdbc.Driver",
            "javax.jdo.option.ConnectionURL", "jdbc:mysql://127.0.0.1:3306/hotel_ibatti",
            "javax.jdo.option.ConnectionUserName", "hotel_app",
            "javax.jdo.option.ConnectionPassword", clave
        );

        PersistenceManagerFactory fabrica = JDOHelper.getPersistenceManagerFactory(
            conexion, "Programacion2", PruebaJdo.class.getClassLoader()
        );

        try {
            guardarHotelSiLaTablaEstaVacia(fabrica);
        } finally {
            fabrica.close();
        }
    }

    private static void guardarHotelSiLaTablaEstaVacia(PersistenceManagerFactory fabrica) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();

        try {
            transaccion.begin();

            int cantidad;
            Query<Hotel> consulta = gestor.newQuery(Hotel.class);
            try {
                cantidad = consulta.executeList().size();
            } finally {
                consulta.closeAll();
            }

            if (cantidad == 0) {
                gestor.makePersistent(new Hotel("Hotel Ibatti", null, null, null));
            }

            transaccion.commit();

            if (cantidad == 0) {
                System.out.println("Hotel Ibatti guardado en MySQL.");
            } else {
                System.out.println("Hoteles encontrados: " + cantidad);
            }
        } finally {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            gestor.close();
        }
    }
}