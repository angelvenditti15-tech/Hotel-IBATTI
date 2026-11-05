package com.programacion2.config;

import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdoConfig {

    @Bean(destroyMethod = "close")
    public PersistenceManagerFactory persistenceManagerFactory() {
        String clave = "271315";

        //if (clave == null || clave.isBlank()) {
          //  throw new IllegalStateException("Falta configurar HOTEL_DB_PASSWORD.");
        //}
        
        Map<String, String> conexion = Map.of(
            "javax.jdo.option.ConnectionDriverName", "com.mysql.cj.jdbc.Driver",
            "javax.jdo.option.ConnectionURL", "jdbc:mysql://127.0.0.1:3306/hotel_ibatti",
            "javax.jdo.option.ConnectionUserName", "hotel_app",
            "javax.jdo.option.ConnectionPassword", clave
        );

        return JDOHelper.getPersistenceManagerFactory(
            conexion, "Programacion2", JdoConfig.class.getClassLoader()
        );
    }
}