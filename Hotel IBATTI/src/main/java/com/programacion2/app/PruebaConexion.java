package com.programacion2.app;

import java.sql.Connection;
import java.sql.DriverManager;

public class PruebaConexion {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/hotel_ibatti";
        String usuario = "hotel_app";
        String clave = System.getenv("HOTEL_DB_PASSWORD");

        if (clave == null || clave.isEmpty()) {
            throw new IllegalStateException("Falta configurar HOTEL_DB_PASSWORD.");
        }

        try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
            System.out.println("Conexión exitosa a la base: " + conexion.getCatalog());
        }
    }
}