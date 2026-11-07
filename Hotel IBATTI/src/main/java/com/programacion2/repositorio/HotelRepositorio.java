package com.programacion2.repositorio;

import com.programacion2.dominio.Hotel;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.stereotype.Repository;

@Repository
public class HotelRepositorio {

    private final PersistenceManagerFactory fabrica;

    public HotelRepositorio(PersistenceManagerFactory fabrica) {
        this.fabrica = fabrica;
    }

    public int contar() {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        try {
            Query<Hotel> consulta = gestor.newQuery(Hotel.class);
            try {
                return consulta.executeList().size();
            } finally {
                consulta.closeAll();
            }
        } finally {
            gestor.close();
        }
    }

    public void guardar(Hotel hotel) {
        PersistenceManager gestor = fabrica.getPersistenceManager();
        Transaction transaccion = gestor.currentTransaction();

        try {
            transaccion.begin();
            gestor.makePersistent(hotel);
            transaccion.commit();
        } finally {
            try {
                if (transaccion.isActive()) {
                    transaccion.rollback();
                }
            } finally {
                gestor.close();
            }
        }
    }
}
