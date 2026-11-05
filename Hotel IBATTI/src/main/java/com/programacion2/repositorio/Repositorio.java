package com.programacion2.repositorio;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

public class Repositorio<T> implements IRepositorio<T> {

    private final PersistenceManagerFactory pmf;

    public Repositorio(String persistenceUnit) {

        this.pmf = JDOHelper.getPersistenceManagerFactory(persistenceUnit);
    }

    @Override
    public void guardar(T objeto) {

        /*
         * Antes de obtener el PersistenceManager, el objeto todavía
         * no está siendo administrado por JDO.
         *
         * Si el objeto fue creado con new, normalmente su estado será:
         *
         * TRANSIENT
         */
        System.out.println(
                "Antes de guardar: " +
                        JDOHelper.getObjectState(objeto));

        /*
         * Obtenemos un PersistenceManager.
         *
         * El PersistenceManager administra los objetos persistentes
         * y permite realizar operaciones sobre la base de datos.
         */
        PersistenceManager pm = pmf.getPersistenceManager();

        /*
         * Obtenemos la transacción asociada al PersistenceManager.
         *
         * Una transacción agrupa una o varias operaciones para que
         * se ejecuten como una única unidad de trabajo.
         */
        Transaction tx = pm.currentTransaction();

        try {

            /*
             * Iniciamos la transacción.
             */
            tx.begin();

            /*
             * Hacemos persistente el objeto.
             *
             * Si el objeto estaba en estado TRANSIENT, normalmente
             * pasa al estado:
             *
             * PERSISTENT_NEW
             *
             * En este momento el objeto está administrado por el
             * PersistenceManager, pero todavía no se confirmaron
             * los cambios en la base de datos.
             */
            pm.makePersistent(objeto);

            System.out.println(
                    "Después de makePersistent: " +
                            JDOHelper.getObjectState(objeto));

            /*
             * Confirmamos la transacción.
             *
             * commit() guarda definitivamente en la base de datos
             * todas las operaciones realizadas desde tx.begin().
             *
             * Después del commit, el objeto puede pasar a un estado
             * como HOLLOW_PERSISTENT_NONTRANSACTIONAL.
             *
             * El estado exacto puede variar según la configuración
             * utilizada por DataNucleus.
             */
            tx.commit();

            System.out.println(
                    "Después de commit: " +
                            JDOHelper.getObjectState(objeto));

        } catch (RuntimeException e) {

            /*
             * Si ocurre un error y la transacción continúa activa,
             * ejecutamos rollback().
             *
             * rollback() deshace las operaciones realizadas dentro
             * de la transacción y evita que la base de datos quede
             * guardada parcialmente.
             */
            if (tx.isActive()) {
                tx.rollback();
            }

            /*
             * Volvemos a lanzar la excepción para que el código que
             * llamó al repositorio pueda conocer el error.
             */
            throw e;

        } finally {

            /*
             * El bloque finally se ejecuta tanto si la operación
             * finaliza correctamente como si ocurre una excepción.
             *
             * Cerramos el PersistenceManager para liberar los
             * recursos utilizados.
             */
            pm.close();
        }
    }

    @Override
    public T consultar(Class<T> clase, int id) {
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setDetachAllOnCommit(true);
       pm.getFetchPlan().setMaxFetchDepth(2);
        Transaction tx = pm.currentTransaction();

        try {

            tx.begin();
            T objeto = pm.getObjectById(clase, id);
            pm.retrieve(objeto);
            tx.commit();
            return objeto;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            pm.close();
        }

    }

    @Override
    public void guardar(List<T> objetos) {
        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            pm.makePersistentAll(objetos);

            tx.commit();

        } catch (RuntimeException e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw e;

        } finally {
            pm.close();
        }
    }

    @Override
    public List<T> listar(Class<T> clase) {
              PersistenceManager pm =
            pmf.getPersistenceManager();

    /*
     * Permite recuperar las relaciones directas de los objetos
     * según el FetchPlan configurado.
     */
    pm.getFetchPlan().setMaxFetchDepth(2);

    Transaction tx =
            pm.currentTransaction();

    Query<T> query = null;

    try {

        tx.begin();

        query = pm.newQuery(clase);

        List<T> resultados =
                query.executeList();

        /*
         * Fuerza la recuperación de los campos incluidos en el
         * FetchPlan para todos los objetos de la lista.
         */
        pm.retrieveAll(resultados, true);

        /*
         * Separamos todos los resultados antes de cerrar
         * el PersistenceManager.
         */
        List<T> resultadosSeparados =
                new ArrayList<>(
                        pm.detachCopyAll(resultados)
                );

        tx.commit();

        return resultadosSeparados;

    } catch (RuntimeException e) {

        if (tx.isActive()) {
            tx.rollback();
        }

        throw e;

    } finally {

        if (query != null) {
            query.closeAll();
        }

        pm.close();
    }

    }

}
